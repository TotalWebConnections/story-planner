(ns story-planner.components.EntityOverlay
  (:require [reagent.core :as reagent :refer [atom]]))

(defn add-field [state]
  (swap! state conj {:id (+ 1 (count @state)) :value ""}))

(defn update-value [state id value]
  "updates the value for the changed input"
  (swap! state update-in [(- id 1)] conj {:value value}))

(defn EntityOverlay [active onSubmit]
  (let [inputFields (atom [{:id 1 :value ""}])]
    (fn []
      [:div.OverlayEntity {:class (str "OverlayEntity--" @active)}
        [:div.OverlayEntity__inner
          [:p.OverlayEntity__inner__close {:on-click #(reset! active false)} "x"]
          [:h3.OverlayEntity__inner-header "Add Entity"]
          [:div.OverlayEntity__fieldWrapper
            (for [entityField @inputFields]
              [:div {:key (:id entityField)}
                [:input.OverlayEntity__input {:type "text"
                  :on-change #(update-value inputFields (:id entityField) (-> % .-target .-value))}]])]
          [:button {:on-click #(add-field inputFields)} "Add Field"]
          [:button {:on-click #(onSubmit @inputFields)} "Save"]]])))
