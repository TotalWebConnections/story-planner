(ns story-planner.views.App_page
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.components.Canvas :as Canvas]
            [story-planner.components.Sidebar :refer [Sidebar]]
            [story-planner.components.app.header :refer [Header]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.Loader :refer [Loader]]
            [reitit.frontend.easy :as rfe]
            [story-planner.components.media.media-manager :refer [Media-Manager]]
            [story-planner.services.scripts.api.api :as api]))

(defn set-initial-board [app-state]
  "sets the first board to active on load - should only ever fire once"
  (if (not (:currentBoard @app-state))
    (handle-state-change {:type "set-active-board" :value (:name (first (:boards (:currentProject @app-state))))})))

(defn handle-add-image-in-storypoint [url]
  (handle-state-change {:type "app-show-media-manager" :value false})
  (api/update-storypoint-image {:value url}))

(defn handle-failed-load [project]
  (if (not project)
    (do
      (js/alert "There was an error loading the project.")
      (rfe/push-state :projects))))

(defn App-page [app-state]
  (js/setTimeout #(handle-failed-load (:currentProject @app-state)) 5000)
  (fn [app-state]
    (let [showMedia (atom (:app-show-media-manager @app-state))]
      (set-initial-board app-state)
      [:div.App {:class (if (:sidebarActive @app-state) "sidebarActive")}
        [Media-Manager showMedia (:images @app-state) (:media-folders @app-state) handle-add-image-in-storypoint true]
        [Header (:name (:currentProject @app-state))]
        [Sidebar (:currentProject @app-state) (:currentBoard @app-state) (:openedFolders @app-state) (:images @app-state) (:media-folders @app-state)]
        [:div.App__canvasWrapper
           (if (not (:currentProject @app-state))
             [Loader]
             [Canvas/render (:currentProject @app-state) (:currentBoard @app-state) (:linkStartId @app-state)])]])))
