(ns story-planner.components.Overlay
  (:require [reagent.core :as reagent :refer [atom]]))

(defn get-input-value []
  "Grabs the input from the value - probably better to use an atom but this
   Should be fine for this simple case"
  (.-value (.getElementById js/document "Overlay__input")))

(defn Overlay [active headerText onSubmit]
  [:div.Overlay {:class (str "Overlay--" active)}
    [:div.Overlay__inner
      [:h3 headerText]
      [:input#Overlay__input {:type "text"}]
      [:button {:on-click #(onSubmit (get-input-value))} "Add"]]])
