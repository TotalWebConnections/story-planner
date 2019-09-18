(ns story-planner.services.scripts.canvas)




(defn get-current-board [boards board]
  "Pulls out and returns the currentBoard"
  (first (filter (fn [toCheck]
    (if (= (:name toCheck) board)
      true
      false)) boards)))