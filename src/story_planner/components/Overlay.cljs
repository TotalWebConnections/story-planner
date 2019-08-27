(ns story-planner.components.Overlay
  (:require [reagent.core :as reagent :refer [atom]]))

(defn Overlay [active headerText onSubmit]
  [:div.Overlay {:class (str "Overlay--" active)}
    [:div.Overlay__inner
      [:h3 headerText]
      [:input {:type "text"}]
      [:button {:on-click #(onSubmit)} "Add"]]])
