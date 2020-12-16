(ns story-planner.components.EntityOverlay
  (:require [reagent.core :as reagent :refer [atom]]
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

(defn handle-submit [inputFields titleField imageField editModeChecked? onSubmit]
  (onSubmit @inputFields @titleField @imageField @editModeChecked?)
  (reset! inputFields [{:id 1 :value ""}])
  (reset! titleField "Untitled")
  (reset! editModeChecked? false))

(defn set-edit-mode [editModeChecked? entity titleField inputFields imageField showMedia]
  (reset! editModeChecked? (:id entity))
  (reset! titleField (:title entity))
  (reset! inputFields (:values entity))
  (reset! imageField (:image entity)))


(defn EntityOverlay [active onSubmit images folders]
  (let [inputFields (atom [{:id 1 :value "" :label ""}])
        titleField (atom "Untitled")
        imageField (atom nil)
        showMedia (atom false)
        editModeChecked? (atom false)]
    (fn [active onSubmit images]
      (if (and (:show @active) (not @editModeChecked?) (:edit @active))
        (set-edit-mode editModeChecked? (:edit @active) titleField inputFields imageField showMedia))
      [:div.OverlayEntity {:class (str "OverlayEntity--" (:show @active))}
        [:div.OverlayEntity__inner
          [Media-Manager-Small showMedia images folders (partial handle-set-image imageField)]
          [:p.OverlayEntity__inner__close {:on-click #(do (reset! editModeChecked? false) (swap! active conj {:show false :edit false}))} "x"]
          [:h3.OverlayEntity__inner-header "Add Entity"]
          [:div.OverlayEntity__inner-media {:on-click #(reset! showMedia "active")}
           (if @imageField
             [:img {:src (str "https://story-planner.s3.amazonaws.com/" @imageField) :height "100%"}])]
          [:input.OverlayEntity__inner-title {:value @titleField :on-change #(reset! titleField (-> % .-target .-value))}]
          [:div.OverlayEntity__fieldWrapper
            (for [entityField @inputFields]
              [:div {:key (:id entityField)}
                [:input.OverlayEntity__input--label {:type "text"
                                                     :placeholder "Label"
                                                     :value (:label entityField)
                                                     :on-change #(update-label inputFields (:id entityField) (-> % .-target .-value))}]
                [:input.OverlayEntity__input {:type "text"
                                              :value (:value entityField)
                                              :on-change #(update-value inputFields (:id entityField) (-> % .-target .-value))}]])
           [:button {:on-click #(add-field inputFields)} "Add Field"]
           [:button {:on-click #(handle-submit inputFields titleField imageField editModeChecked? onSubmit)} "Save"]]]])))
