(ns story-planner.components.canvas.Folder
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]))

(defn generate-entity-display [folder]
  "generates a folder display, nested values so it's a bit different"
  [:p {:key (str (:value (first folder)) "-" (rand-int 10000))}
    (:value (first folder))])

(defn generate-board-display [folder]
  [:p {:key (str (:name folder) "-" (rand-int 10000))}
    (:name folder)])


(defn Folder [folderInfo onClick isBoardFolder]
  (let [folderType (if isBoardFolder :boards :entities)]
    [:div.Folder {:key (str (:name folderInfo) "-" (rand-int 10000))}
      [:p
        {:on-click #(onClick)}
        (:name folderInfo)]
      [:div.Folder__entityWrapper
        (for [entity (folderType folderInfo)]
          (if (= :boards folderType)
            (generate-board-display entity)
            (generate-entity-display entity)))]]))