(ns story-planner.services.scripts.canvas)


(defn get-current-board-storypoints [storypoints board]
  "returns the storypoints for a given board"
  (filter (fn [point]
    (if (= (:board point) board)
      true
      false)) storypoints))