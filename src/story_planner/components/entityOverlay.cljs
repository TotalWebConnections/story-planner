(ns story-planner.components.EntityOverlay
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.components.media.media-manager-small :refer [Media-Manager-Small]]))

(defn add-field [state]
  (swap! state conj {:id (+ 1 (count @state)) :value ""}))

(defn update-value [state id value]
  "updates the value for the changed input"
  (swap! state update-in [(- id 1)] conj {:value value}))

(defn handle-submit [inputFields titleField onSubmit]
  (onSubmit @inputFields @titleField)
  (reset! inputFields [{:id 1 :value ""}])
  (reset! titleField "Untitled"))


(defn EntityOverlay [active onSubmit images]
  (let [inputFields (atom [{:id 1 :value ""}])
        titleField (atom "Untitled")
        showMedia (atom false)]
    (fn []
      [:div.OverlayEntity {:class (str "OverlayEntity--" @active)}
        [:div.OverlayEntity__inner
          [Media-Manager-Small showMedia images]
          [:p.OverlayEntity__inner__close {:on-click #(reset! active false)} "x"]
          [:h3.OverlayEntity__inner-header "Add Entity"]
          [:div.OverlayEntity__inner-media {:on-click #(reset! showMedia "active")}]
          [:input.OverlayEntity__inner-title {:value @titleField :on-change #(reset! titleField (-> % .-target .-value))}]
          [:div.OverlayEntity__fieldWrapper
            (for [entityField @inputFields]
              [:div {:key (:id entityField)}
                [:input.OverlayEntity__input {:type "text"
                                              :value (:value entityField)
                                              :on-change #(update-value inputFields (:id entityField) (-> % .-target .-value))}]])
           [:button {:on-click #(add-field inputFields)} "Add Field"]
           [:button {:on-click #(handle-submit inputFields titleField onSubmit)} "Save"]]]])))
