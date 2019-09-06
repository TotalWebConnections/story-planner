(ns story-planner.components.Sidebar
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.components.Overlay :refer [Overlay]]
            [story-planner.components.EntityOverlay :refer [EntityOverlay]]))

(defn add-folder [state projectId value]
  "Adds a new folder"
  (reset! state false)
  (api/create-folder {:type "entity" :projectId projectId :value value}))

(defn generate-folder-path [currentFolderPath folderPath]
  (reset! currentFolderPath folderPath))

(defn add-entity [state projectId folder value]
  "adds a new entity to the give folder"
  (reset! state false)
  (api/create-entity {:folder @folder :projectId projectId :value value}))

(defn handleShowOverlay [state]
  (reset! state "active"))

(defn Sidebar [currentProject]
  (let [showFolderOverlay (atom false)
        showEntityOverlay (atom false)
        currentFolderPath (atom "n/a")] ; we use this to update the folder path we want to save an entity to
    (fn []
      [:div.Sidebar
        [Overlay showFolderOverlay "Folder Name" (partial add-folder showFolderOverlay (:_id currentProject))]
        [EntityOverlay showEntityOverlay
          (partial add-entity showEntityOverlay (:_id currentProject) currentFolderPath)]
        [:div.Sidebar__header
          [:h4 "Entities"]
          [:div.Sidebar__header__controls
            [:div.addEntity  [:p {:on-click #(handleShowOverlay showEntityOverlay)} "+"]]
            [:div.addFolder [:p {:on-click #(handleShowOverlay showFolderOverlay)} "+"]]]]
        [:div.Sidebar__contentWrapper
          (for [folder (:folders currentProject)]
            [:p
              {:on-click #((comp
                             (generate-folder-path currentFolderPath (:name folder))
                             (handleShowOverlay showEntityOverlay)))}
              (:name folder)])
        ]
        [:div.Sidebar__header
          [:p "Boards"]
          [:div.Sidebar__header__controls
            [:div.addEntity  [:p "+"]]
            [:div.addFolder [:p {:on-click #(handleShowOverlay showFolderOverlay)} "+"]]]]])))
