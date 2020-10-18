(ns story-planner.views.App_page
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.components.Canvas :as Canvas]
            [story-planner.components.Sidebar :refer [Sidebar]]
            [story-planner.components.app.header :refer [Header]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))

(defn set-initial-board [app-state]
  "sets the first board to active on load - should only ever fire once"
  (if (not (:currentBoard @app-state))
    (handle-state-change {:type "set-active-board" :value (:name (first (:boards (:currentProject @app-state))))})))

(defn App-page [app-state]
  (set-initial-board app-state)
  [:div.App
    [Header (:name (:currentProject @app-state))]
    [Sidebar (:currentProject @app-state) (:currentBoard @app-state) (:openedFolders @app-state) (:images @app-state)]
    [:div.App__canvasWrapper
      [Canvas/render (:currentProject @app-state) (:currentBoard @app-state) (:linkStartId @app-state)]]])
