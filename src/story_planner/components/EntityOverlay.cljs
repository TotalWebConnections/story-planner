(ns story-planner.components.EntityOverlay
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.media.media-manager-small :refer [Media-Manager-Small]]))

(defn add-field [state]
  (swap! state conj {:id (+ 1 (count @state)) :value ""}))

(defn update-value [state id value]
  "updates the value for the changed input"
  (swap! state update-in [(- id 1)] conj {:value value}))

(defn update-label [state id label]
  "updates the value for the changed input"
  (swap! state update-in [(- id 1)] conj {:label label}))

(defn handle-set-image [image-state src]
  (reset! image-state src))

(defn handle-submit [inputFields titleField imageField editModeChecked? onSubmit & [delete]]
  (onSubmit @inputFields @titleField @imageField @editModeChecked? delete)
  (reset! inputFields [{:id 1 :value ""}])
  (reset! titleField "Untitled")
  (reset! editModeChecked? false))

(defn set-edit-mode [editModeChecked? entity titleField inputFields imageField showMedia]
  (reset! editModeChecked? (:id entity))
  (reset! titleField (:title entity))
  (reset! inputFields (:values entity))
  (reset! imageField (:image entity)))

(defn reset-edit-mode [titleField imageField]
  (reset! titleField "Untitled")
  (reset! imageField nil))

(defn EntityOverlay [active onSubmit images folders currentFolderPath]
  (let [inputFields (atom [{:id 1 :value "" :label ""}])
        titleField (atom "Untitled")
        imageField (atom nil)
        showMedia (atom false)
        editModeChecked? (atom false)]
    (fn [active onSubmit images folders currentFolderPath]
      (if (and (:show active) (not @editModeChecked?) (:edit active))
        (set-edit-mode editModeChecked? (:edit active) titleField inputFields imageField showMedia))
      [:div.OverlayEntity {:class (str "OverlayEntity--" (:show active))}
        [:div.OverlayEntity__inner
          [Media-Manager-Small showMedia images folders (partial handle-set-image imageField)]
          [:p.OverlayEntity__inner__close.closeButton {:on-click #(do (reset! currentFolderPath "n/a") (reset! editModeChecked? false) (reset! inputFields [{:id 1 :value ""}]) (reset-edit-mode titleField imageField) (handle-state-change {:type "set-entity-overlay-hidden" :value nil}))} "x"]
          (if  @editModeChecked?
            [:h3.OverlayEntity__inner-header "Edit Entity"]
            (if (not= @currentFolderPath "n/a")
              [:h3.OverlayEntity__inner-header (str "Add Entity - Folder: " @currentFolderPath)]
              [:h3.OverlayEntity__inner-header "Add Entity"]))
          [:div.OverlayEntity__inner-media {:on-click #(reset! showMedia "active")}
           (if @imageField
             [:img {:src (str "https://story-planner.s3.amazonaws.com/" @imageField) :height "100%"}])]
          [:input.OverlayEntity__inner-title {:value @titleField :on-change #(reset! titleField (-> % .-target .-value))}]
          [:div.OverlayEntity__fieldWrapper
            (for [entityField @inputFields]
              [:div.OverlayEntity__fieldWrapper--fields {:key (:id entityField)}
                [:input.OverlayEntity__input--label {:type "text"
                                                     :placeholder "Label"
                                                     :value (:label entityField)
                                                     :on-change #(update-label inputFields (:id entityField) (-> % .-target .-value))}]
                [:input.OverlayEntity__input {:type "text"
                                              :placeholder "Value"
                                              :value (:value entityField)
                                              :on-change #(update-value inputFields (:id entityField) (-> % .-target .-value))}]])
           [:button {:on-click #(add-field inputFields)} "Add Field"]
           [:div.OverlayEntity__fieldWrapper__buttons
            [:button {:on-click #(handle-submit inputFields titleField imageField editModeChecked? onSubmit)} "Save"]
            (if  @editModeChecked? [:button.danger {:on-click #(handle-submit inputFields titleField imageField editModeChecked? onSubmit true)} "Delete"])]]]])))
