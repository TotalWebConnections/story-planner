(ns story-planner.components.Sidebar
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.components.Overlay :refer [Overlay]]))

(defn add-entity []
  "adds a new entity")

(defn add-folder [state value]
  "Adds a new folder"
  (reset! state false)
  (api/create-folder {:type "entity" :value value}))

(defn handleShowFolderOverlay [state]
  (reset! state "active"))

(defn Sidebar []
  (let [showFolderOverlay (atom false)]
    (fn []
      [:div.Sidebar
        [Overlay @showFolderOverlay "Folder Name" (partial add-folder showFolderOverlay)]
        [:div.Sidebar__header
          [:h4 "Entities"]
          [:div.Sidebar__header__controls
            [:div.addEntity  [:p "+"]]
            [:div.addFolder [:p {:on-click #(handleShowFolderOverlay showFolderOverlay)} "+"]]]]
        [:p "Boards"]])))
