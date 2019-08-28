(ns story-planner.services.scripts.api.api
  (:require [story-planner.services.scripts.api.websocket :refer [send-message]]))

; CLIENT SIDE API REQUESTS
; This file only handles the actual requsts to the server that the application makes
; return and state updating functions take place in the WS connection



(defn create-project [constructor]
  (send-message {:type "create-project" :value constructor}))
(defn delete-project [])
(defn edit-project [])
(defn get-projects []
  (send-message {:type "get-projects" :value "123"})) ; value here represents our userID!

(defn create-board [])
(defn delete-board [])
(defn edit-board [])

;These can be used for both board and entity folders
(defn create-folder [constructor]
  (send-message {:type "create-folder" :folder (:type constructor) :value (:value constructor)}))
(defn delete-folder [])
(defn edit-folder [])

(defn create-entity [])
(defn delete-entity [])
(defn edit-entiy [])