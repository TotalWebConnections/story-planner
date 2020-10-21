(ns story-planner.services.scripts.components.entities
  (:require [story-planner.services.state.global :refer [get-from-state]]))


(defn get-entity-by-id [id]
  (let [entities (:entities (get-from-state "currentProject"))]
    (first (filter #(= (:id %) id) entities))))
