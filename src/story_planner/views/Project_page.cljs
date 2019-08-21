(ns story-planner.views.Project_page
  (:require [story-planner.components.Canvas :as Canvas]
            [story-planner.components.Sidebar :refer [Sidebar]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))


(defn Project-page []
  [:div.Project
    [:h2 "Project Page Details"]])