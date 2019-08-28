(ns story-planner.views.Project_page
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.components.Overlay :refer [Overlay]]
            [story-planner.components.app.header :refer [Header]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))


(defn open-new-project-overlay [state]
  "Opens the new new project overlay"
  (reset! state "active"))

(defn save-new-project [state value]
  "Calls the api to save a new project"
  (reset! state false)
  (api/create-project value))

(defn Project-page []
  (let [showProjectOverlay (atom false)]
    (fn []
      [:div.Projects
        [Overlay @showProjectOverlay "Project New" (partial save-new-project showProjectOverlay)]
        [Header]
        [:div.Projects__body
          [:h2 "Project Page"]
          [:button {:on-click #(open-new-project-overlay showProjectOverlay)}"Add New Project"]]])))