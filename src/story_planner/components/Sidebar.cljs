(ns story-planner.components.Sidebar
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.components.Overlay :refer [Overlay]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.EntityOverlay :refer [EntityOverlay]]
            [story-planner.components.canvas.Folder :refer [Folder]]
            [story-planner.services.scripts.folders :as folderHelpers]
            [story-planner.services.scripts.sidebar :refer [get-boards-by-folders]]))

(defn add-folder [state projectId folderType value]
  "Adds a new folder"
  (reset! state false)
  (api/create-folder {:type folderType :projectId projectId :value value}))

(defn generate-folder-path [currentFolderPath folderPath]
  (reset! currentFolderPath folderPath))

(defn add-entity [state projectId folder value title image]
  "adds a new entity to the give folder"
  (api/create-entity {:folder @folder :projectId projectId :value value :title title :image image})
  (reset! state false)
  (reset! folder "n/a"))

; nearly the same as add-entity but give us some separation if need it l8er
(defn add-board [state projectId folder value]
  "adds a new board to the project"
  (reset! state false)
  (reset! folder "n/a")
  (api/create-board {:folder @folder :projectId projectId :value value}))

(defn handleShowOverlay [state]
  (reset! state "active"))

(defn setCurrentFolderType [currentFolderType type]
  (reset! currentFolderType type))

; TODO this is gettin a bit large - probably break this out by boards and entity into new components
(defn Sidebar [currentProject currentBoard openedFolders images]
  (let [showFolderOverlay (atom false)
        showBoardOverlay (atom false)
        showEntityOverlay (atom false)
        currentFolderPath (atom "n/a")
        projectId (:_id currentProject)
        currentFolderType (atom nil)] ; we use this to update the folder path we want to save an entity to
    (fn [currentProject currentBoard openedFolders]
      (let [sortedFolders (folderHelpers/get-folders-by-type (:folders currentProject))]
        [:div.Sidebar
          [Overlay showFolderOverlay "Add New Folder" (partial add-folder showFolderOverlay (:_id currentProject) @currentFolderType) 1]
          [Overlay showBoardOverlay "Add Board To This Project" (partial add-board showBoardOverlay (:_id currentProject) currentFolderPath) 2]
          [EntityOverlay showEntityOverlay
            (partial add-entity showEntityOverlay projectId currentFolderPath) images]
          [:div.Sidebar__header
            [:h3 "Entities"]
            [:div.Sidebar__header__controls
              [:div.addEntity  [:p {:on-click #(handleShowOverlay showEntityOverlay)} "+"]]
              [:div.addFolder [:i.fas.fa-folder {:on-click #(comp (handleShowOverlay showFolderOverlay) (setCurrentFolderType currentFolderType "entity"))}]]]]
          [:div.Sidebar__contentWrapper
            (for [entity (:entities currentProject)]
              (if (= (:folder entity) "n/a")
                [:p.entityWrapper (:title entity)]))
            (for [folder (folderHelpers/assign-entities-to-parent-folder (get sortedFolders "entity") (:entities currentProject))]
              (Folder folder currentBoard openedFolders #(comp
                                                          (generate-folder-path currentFolderPath (:name folder))
                                                          (handleShowOverlay showEntityOverlay)) false))]
          [:div.Sidebar__header
            [:h3 "Boards"]
            [:div.Sidebar__header__controls
              [:div.addEntity  [:p {:on-click #(handleShowOverlay showBoardOverlay)}  "+"]]
              [:div.addFolder [:i.fas.fa-folder {:on-click #(comp (handleShowOverlay showFolderOverlay) (setCurrentFolderType currentFolderType "board"))}]]]]
          [:div.Sidebar__contentWrapper
            (for [board (:boards currentProject)]
              (if (= (:folder board) "n/a")
                [:p.entityWrapper {:on-click #(handle-state-change {:type "set-active-board" :value (:name board)})} (:name board)]))
            (for [folder (get-boards-by-folders (get sortedFolders "board") (:boards currentProject))]
              (Folder folder currentBoard openedFolders #(comp
                                                            (generate-folder-path currentFolderPath (:name folder))
                                                            (handleShowOverlay showBoardOverlay)) true))]]))))
