(ns story-planner.components.EntityOverlay
  (:require [reagent.core :as reagent :refer [atom]]))

(defn get-input-value []
  "Grabs the input from the value - probably better to use an atom but this
   Should be fine for this simple case"
  (.-value (.getElementById js/document "OverlayEntity__input")))

(defn add-field [state]
  (swap! state conj {:id (+ 1 (count @state)) :value ""}))

(defn EntityOverlay [active onSubmit]
  (let [inputFields (atom [{:id 1 :value "" :placeholder "Name"}])]
    (fn []
      [:div.OverlayEntity {:class (str "OverlayEntity--" @active)}
        [:div.OverlayEntity__inner
          [:p.OverlayEntity__inner__close {:on-click #(reset! active false)} "x"]
          [:p {:on-click #(add-field inputFields)} "Add Field"]
          [:h3 "Add Entity"]
          (for [entityField @inputFields]
            [:input#OverlayEntity__input {:key (:id entityField) :type "text"}])
          ; [:input#OverlayEntity__input {:type "text"}]
          [:button {:on-click #(onSubmit (get-input-value))} "Add"]]])))
