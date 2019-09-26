(ns story-planner.components.canvas.Folder
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.services.state.global :refer [get-from-state]]))

(defn set-active-board [event board]
  "Sets the clicked board to active in state - dictates which story points to show"

  ; This is kind of a hacky way to do this, but I like it more than doing some weird
  ; stuff with the state to get it here correctly
  (let [currentElement (.getElementById js/document "Folder__board--active")]
    (if currentElement
      (do
        (.remove (.-classList (.getElementById js/document "Folder__board--active")) "Folder__board--active")
        (.setAttribute event "id" ""))))
  (.setAttribute event "class" "Folder__board--active")
  (.setAttribute event "id" "Folder__board--active")

  (handle-state-change {:type "set-active-board" :value board}))

(defn generate-storypoints [board]
  "Generates teh storpoints dispaly for a board"
  (for [storypoint (:storypoints board)]
    [:p (:name storypoint)]))

(defn generate-entity-display [folder]
  "generates a folder display, nested values so it's a bit different"
  [:div {:key (str (:value (first folder)) "-" (rand-int 10000))}
    [:p
      (:value (first folder))]])

(defn generate-board-display [folder]
  "Generates our board section display"
  [:div.Folder__board {:key (str (:name folder) "-" (rand-int 10000))}
    [:p {:on-click #(set-active-board (-> % .-target) (:name folder))}
      (:name folder)]])

(defn toggle-folder-display [folder]
  "toggles whether a folder should be open or closed which shows its contents"
  (handle-state-change {:type "toggle-folder-open" :value folder}))

(defn get-folder-active-display [active]
  "Show up or down arrow based on state"
  (if active
    "fa-caret-up"
    "fa-caret-down"))


(defn Folder [folderInfo onClick isBoardFolder]
  (let [folderType (if isBoardFolder :boards :entities)]
    [:div.Folder {:key (str (:name folderInfo) "-" (rand-int 10000))
                  :class (if (not (:active folderInfo)) "Folder__closed" "Folder__open")}
      [:div.Folder__folder
        [:div.Folder__folder__left {:on-click #(onClick)}
          [:i.fas.fa-folder]
          [:p (:name folderInfo)]]
        [:div.Folder__folder__right {:on-click #(toggle-folder-display (:name folderInfo))}
          [:i.fas {:class (get-folder-active-display (:active folderInfo))} ]]]
      [:div.Folder__entityWrapper
        (for [entity (folderType folderInfo)]
          (if (= :boards folderType)
            (generate-board-display entity)
            (generate-entity-display entity)))]]))