(ns story-planner.components.canvas.Folder
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]))



(defn Folder [folderInfo onClick]
  [:div.Folder {:key (str (:name folderInfo) "-" (rand-int 10000))}
    [:p
      {:on-click #(onClick)}
      (:name folderInfo)]
    [:div.Folder__entityWrapper
      (for [entity (:entities folderInfo)]
        [:p (:value (first entity))])]]) ; we take the first for now but will probably add back in a name attribute