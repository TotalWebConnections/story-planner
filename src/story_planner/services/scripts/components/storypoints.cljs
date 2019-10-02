(ns story-planner.services.scripts.components.storypoints)

(defn get-storypoint-by-id [storypoints id]
  "Returns match storypoint - first as ID should be unique"
  (first (filter (fn [storypoint]
    (if (= (:id storypoint) id) true false)) storypoints)))

; TODO we need to find out the relative direction of the other
; entity and adjust the position based on that
(defn calculate-curve-x-initial [size]
  "initial x is simply the width"
  (:w size))

(defn calculate-curve-y-initial [size]
  "Half way the height"
  (/ (:h size) 2))