(ns story-planner.views.Project_page
  (:require [reagent.core :as reagent :refer [atom]]
            [reagent.core :as r]
            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [story-planner.services.scripts.navigation :refer [navigate]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.components.Overlay :refer [Overlay]]
            [story-planner.components.app.header :refer [Header]]
            [story-planner.components.media.media-manager :refer [Media-Manager]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.Loader :refer [Loader]]
            [story-planner.components.project.Confirmation :refer [Confirmation]]))

(defn load-content [app-state loaded]
  ; TODO this works for testing but needs to be moved for prod
  (js/setTimeout
    (fn []
      (api/get-projects (:token (:user @app-state)))
      (api/get-images (:token (:user @app-state)))
      (api/get-authorized-users)
      (reset! loaded true))
    1000))

(defn open-new-project-overlay [state]
  "Opens the new new project overlay"
  (reset! state "active"))

(defn save-new-project [state value]
  "Calls the api to save a new project"
  (reset! state false)
  (api/create-project {:project value}))

(defn delete-project [id showProjectDelete]
  "handle delete of project"
  (reset! showProjectDelete {:active false :id nil})
  (api/delete-project id))

(defn open-project [id]
  "Sets the active project by id - calls query to pull information"
  ; First we set teh current project as the ID that is being queries
  ; dispatch api call but not care about it
  (api/get-project id)
  ; (rfe/push-state ::frontpage) ; THid doesnt work but I wish it would...
  (navigate "app"))


(defn Project-page [app-state]
  (let [showProjectOverlay (atom false)
        showMediaManager (atom false)
        loaded? (atom false)
        showProjectDelete (atom {:active false :id nil})]
    (if (not @loaded?)
      (load-content app-state loaded?))
    (fn []
      [:div.Projects
        [Media-Manager showMediaManager (:images @app-state) (:media-folders @app-state)]
        [Overlay showProjectOverlay "Project New" (partial save-new-project showProjectOverlay)]
        [Confirmation (:active @showProjectDelete) "Are You Sure? You will delete this project and all its contents. This cannot be undone." #(delete-project (:id @showProjectDelete) showProjectDelete) #(reset! showProjectDelete {:active false :id nil})]
        [:div.Projects__header
          [:h2 "My Projects"]
          [:div.Projects__header__nav
           [:p {:on-click #(reset! showMediaManager "active")} "Media Manager"]
           [:p {:on-click #(navigate "profile")} "Profile"]]]
        (if (not @loaded?)
          [Loader])
        [:div.Projects__body.standard-padding
          (for [project (:projects @app-state)]
            [:div.Projects__projectBlock {:key (:_id project)}
              [:h2  (:name project)]
              [:button {:on-click #(open-project (:_id project))} "Build"]
              [:button.danger {:on-click #(reset! showProjectDelete {:active "active" :id (:_id project)})} "Delete"]])]
        [:div.standard-padding
          [:button {:on-click #(open-new-project-overlay showProjectOverlay)}"Add New Project"]]])))
