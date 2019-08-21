(ns story-planner.components.Sidebar
  (:require [reagent.core :as reagent :refer [atom]]))

(defn Sidebar []
  [:div.Sidebar
    [:p "sidebar content"]
    [:p "Folders"]
    [:p "Boards"]])
