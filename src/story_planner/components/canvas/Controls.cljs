(ns story-planner.components.canvas.Controls
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]))

(defn handle-add-storypoint [projectId board panHandler]
  (let [currentPan (js->clj (.getPan panHandler) :keywordize-keys true)]
    (api/create-storypoint {:projectId projectId
                            :board board
                            :entityId nil
                            :position {:x (* -1 (:x currentPan )) :y (* -1 (:y currentPan))} ;TODO make this take the zoom/pos into account
                            :size {:h 200 :w 300}})))

(defn Controls [projectId currentBoard panHandler]
  [:div.Controls
    [:div.Controls__NewStoryPoint
      [:p {:on-click #(handle-add-storypoint projectId currentBoard panHandler)}"+"]] ; TODO pull this down remove hardcode
    [:div.Controls__ProjectInfo
      [:p "?"]]])
