(ns story-planner.components.Sidebar
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]))

(defn add-folder []
  "Adds a new folder"
  (api/create-folder))

(defn Sidebar []
  [:div.Sidebar
    [:p "sidebar content"]
    [:p {:on-click #(add-folder)} "Folders"]
    [:p "Boards"]])
