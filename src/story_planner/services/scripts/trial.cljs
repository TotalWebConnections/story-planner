(ns story-planner.services.scripts.trial
  (:require [story-planner.services.state.global :refer [get-from-state]]))
; HANDLES concerns related to trial for the UI

(def MAX_ITEMS 50)

(defn check-total-usage []
  (let [currentProject (get-from-state "currentProject")]
    (+ (count (:storypoints currentProject))
       (count (:entities currentProject)))))

(defn is-at-limit? [usage]
  (>= usage MAX_ITEMS))


(defn user-able-to-add? []
  "not as this gives us a return on whether to continue"
  (if (or (:subToken (get-from-state "user")) (= (:type (get-from-state "user")) "sub")) ; a subscribed user or sub has no restrictions
    true
    (not (is-at-limit? (check-total-usage)))))
