(ns story-planner.components.canvas.Folder
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.services.state.global :refer [get-from-state]]))

(defn start-drag [e]
  (handle-state-change {:type "set-drag-id" :value (.-id (.-target e))}))

(defn set-active-board [event board]
  "Sets the clicked board to active in state - dictates which story points to show"
  (handle-state-change {:type "set-active-board" :value board}))

(defn generate-storypoints [board]
  "Generates teh storpoints dispaly for a board"
  (for [storypoint (:storypoints board)]
    [:p (:name storypoint)]))

(defn generate-entity-display [entity edit-entity]
  "generates a folder display, nested values so it's a bit different"
  ;TODO this needs to open the entity display
  [:div {:key (str (:value (first entity)) "-" (rand-int 10000))
         :draggable true :id (:id entity) :on-drag-start start-drag}
    [:p.entityWrapper {:on-click #(edit-entity entity)}
      (:title entity)]])

(defn generate-board-display [folder currentBoard]
  "Generates our board section display"
  [:div.Folder__board {:key (str (:name folder) "-" (rand-int 10000))
                       :class (if (= currentBoard (:name folder)) "Folder__board--active")}
    [:p {:on-click #(set-active-board (-> % .-target) (:name folder))}
      (:name folder)]])

(defn toggle-folder-display [folder value]
  "toggles whether a folder should be open or closed which shows its contents"
  (handle-state-change {:type "toggle-folder-open" :value {:name folder :value value}}))

(defn get-folder-active-display [active]
  "Show up or down arrow based on state"
  (if active
    "fa-caret-up"
    "fa-caret-down"))

(defn confirm-folder-delete [folder-id]
  (let [confirmed? (js/confirm "Delete Folder?")]
    (if confirmed?
      (api/delete-folder {:folderId folder-id})
      nil)))


(defn Folder [folderInfo currentBoard openedFolders onClick isBoardFolder edit-entity]
  (let [folderType (if isBoardFolder :boards :entities)]
    [:div.Folder {:key (str (:name folderInfo) "-" (rand-int 10000))
                  :class (if (not ((keyword (:name folderInfo)) openedFolders)) "Folder__closed" "Folder__open")}
      [:div.Folder__folder
        [:div.Folder__folder__left
          [:i.fas.fa-folder.Folder__folderIcon {:on-click #(confirm-folder-delete (:folderId folderInfo))}]
          [:p {:on-click #(onClick)} (:name folderInfo)]]
        [:div.Folder__folder__right {:on-click #(toggle-folder-display (:name folderInfo) (not ((keyword (:name folderInfo)) openedFolders)))}
          [:i.fas {:class (get-folder-active-display (:active folderInfo))}]]]
      [:div.Folder__entityWrapper
        (for [entity (folderType folderInfo)]
          (if (= :boards folderType)
            (generate-board-display entity currentBoard)
            (generate-entity-display entity edit-entity)))]]))
