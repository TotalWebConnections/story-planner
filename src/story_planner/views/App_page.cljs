(ns story-planner.views.App_page
  (:require [story-planner.components.Canvas :as Canvas]
            [story-planner.components.Sidebar :refer [Sidebar]]
            [story-planner.components.app.header :refer [Header]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))


(defn App-page [app-state]
  [:div.App
    [Header]
    [Sidebar (:currentProject @app-state)]
    [:div.App__canvasWrapper
      [Canvas/render]]])