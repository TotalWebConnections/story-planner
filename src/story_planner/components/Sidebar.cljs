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

(defn add-entity [state projectId folder value title image & [id]]
  "adds a new entity to the give folder - taks an optional id to trigger edit"
  (if id
    (api/edit-entity {:id id :folder @folder :projectId projectId :value value :title title :image image})
    (api/create-entity {:folder @folder :projectId projectId :value value :title title :image image}))
  (reset! state {:show false :edit false :type "entity"})
  (reset! folder "n/a"))

; nearly the same as add-entity but give us some separation if need it l8er
(defn add-board [state projectId folder value]
  "adds a new board to the project"
  (reset! state false)
  (reset! folder "n/a")
  (api/create-board {:folder @folder :projectId projectId :value value}))

(defn handleShowOverlay [state]
  (if (= (:type @state) "entity")
    (swap! state conj {:show "active"})
    (reset! state "active")))

(defn setCurrentFolderType [currentFolderType type]
  (reset! currentFolderType type))

(defn start-drag [e]
  (handle-state-change {:type "set-drag-id" :value (.-id (.-target e))}))

(defn edit-entity [showEntityOverlay entity]
  (swap! showEntityOverlay conj {:show "active" :edit entity}))

; TODO this is gettin a bit large - probably break this out by boards and entity into new components
(defn Sidebar [currentProject currentBoard openedFolders images media-folders]
  (print images)
  (let [showFolderOverlay (atom false)
        showBoardOverlay (atom false)
        showEntityOverlay (atom {:show false :edit false :type "entity"})
        currentFolderPath (atom "n/a")
        projectId (:_id currentProject)
        currentFolderType (atom nil)] ; we use this to update the folder path we want to save an entity to
    (fn [currentProject currentBoard openedFolders images]
      (let [sortedFolders (folderHelpers/get-folders-by-type (:folders currentProject))]
        [:div.Sidebar
          [Overlay showFolderOverlay "Add New Folder" (partial add-folder showFolderOverlay (:_id currentProject) @currentFolderType) 1]
          [Overlay showBoardOverlay "Add Board To This Project" (partial add-board showBoardOverlay (:_id currentProject) currentFolderPath) 2]
          [EntityOverlay showEntityOverlay
            (partial add-entity showEntityOverlay (:_id currentProject) currentFolderPath) images media-folders]
          [:div.Sidebar__header
            [:h3 "Entities"]
            [:div.Sidebar__header__controls
              [:div.addEntity  [:p {:on-click #(handleShowOverlay showEntityOverlay)} "+"]]
              [:div.addFolder [:i.fas.fa-folder {:on-click #(comp (handleShowOverlay showFolderOverlay) (setCurrentFolderType currentFolderType "entity"))}]]]]
          [:div.Sidebar__contentWrapper
            (for [entity (:entities currentProject)]
              (if (= (:folder entity) "n/a")
                [:p.entityWrapper {:draggable true :id (:id entity) :on-drag-start start-drag :key (:id entity)
                                   :on-click #(edit-entity showEntityOverlay entity)} (:title entity)]))
            (for [folder (folderHelpers/assign-entities-to-parent-folder (get sortedFolders "entity") (:entities currentProject))]
              ^{:key folder} (Folder folder currentBoard openedFolders #(comp
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
                [:p.entityWrapper {:on-click #(handle-state-change {:type "set-active-board" :value (:name board)}) :key (:name board)} (:name board)]))
            (for [folder (get-boards-by-folders (get sortedFolders "board") (:boards currentProject))]
              ^{:key folder} (Folder folder currentBoard openedFolders #(comp
                                                                          (generate-folder-path currentFolderPath (:name folder))
                                                                          (handleShowOverlay showBoardOverlay)) true))]]))))
