(ns story-planner.components.canvas.Controls
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]))

(defn handle-add-storypoint [projectId board]
  (api/create-storypoint {:projectId projectId
                          :board board
                          :position {:x 0 :y 0}
                          :size {:h 100 :w 400}}))

(defn Controls [projectId currentBoard]
  [:div.Controls
    [:div.Controls__NewStoryPoint
      [:p {:on-click #(handle-add-storypoint projectId currentBoard)}"+"]] ; TODO pull this down remove hardcode
    [:div.Controls__ProjectInfo
      [:p "?"]]])