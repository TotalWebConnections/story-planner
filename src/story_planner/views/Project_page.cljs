(ns story-planner.views.Project_page
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.core :as r]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [story-planner.services.scripts.navigation :refer [navigate]]
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

(defn delete-project [id]
  "TODO handle delete of project")

(defn open-project [id]
  "Sets the active project by id - calls query to pull information"
  ; First we set teh current project as the ID that is being queries
  ; dispatch api call but not care about it
  (api/get-project id)
  ; (rfe/push-state ::frontpage) ; THid doesnt work but I wish it would...
  (navigate "app"))



(defn Project-page [app-state]
  (let [showProjectOverlay (atom false)]
    (fn []
      [:div.Projects
        [Overlay showProjectOverlay "Project New" (partial save-new-project showProjectOverlay)]
        [:div.Projects__header.standard-padding
          [:h2 "My Projects"]]
        [:div.Projects__body.standard-padding
          (for [project (:projects @app-state)]
            [:div.Projects__projectBlock {:key (:_id project)}
              [:h2  (:name project)]
              [:button {:on-click #(open-project (:_id project))} "Build"]
              [:button.danger {:on-click #(delete-project (:_id project))} "Delete"]])]
        [:div.standard-padding
          [:button {:on-click #(open-new-project-overlay showProjectOverlay)}"Add New Project"]]])))