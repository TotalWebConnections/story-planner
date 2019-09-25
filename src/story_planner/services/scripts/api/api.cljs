(ns story-planner.services.scripts.api.api
  (:require [story-planner.services.scripts.api.websocket :refer [send-message]]))

; CLIENT SIDE API REQUESTS
; This file only handles the actual requsts to the server that the application makes
; return and state updating functions take place in the WS connection



(defn create-project [constructor]
  (send-message {:type "create-project" :value constructor}))
(defn delete-project [])
(defn edit-project [])
(defn get-project [id]
  (send-message {:type "get-project" :value id}))
(defn get-projects []
  (send-message {:type "get-projects" :value "123"})) ; value here represents our userID!

(defn create-board [constructor]
  (send-message {:type "create-board" :projectId (:projectId constructor) :value {:name (:value constructor) :folder (:folder constructor)}}))
(defn delete-board [])
(defn edit-board [])

;These can be used for both board and entity folders
(defn create-folder [constructor]
  (send-message {:type "create-folder" :folder (:type constructor) :projectId (:projectId constructor) :value (:value constructor)}))
(defn delete-folder [])
(defn edit-folder [])

(defn create-entity [constructor]
  (send-message {:type "create-entity"
                 :folder (:folder constructor)
                 :projectId (:projectId constructor)
                 :value (:value constructor)}))
(defn delete-entity [])
(defn edit-entiy [])



(defn create-storypoint [constructor]
  "creates a brand new storpy point associated with the board/projectID combo"
  (send-message {:type "create-storypoint" :projectId (:projectId constructor)
                 :board (:board constructor)
                 :position (:position constructor)
                 :size (:size constructor)}))

; NOTE - opted to break apart updates here to prevent weird race conditions
; where someone edits the name and another moves it - these states could still happen
; but the odds are much less and likely to be two people trying to edit the same attr that
; causes teh condition
(defn update-storypoint-position [constructor]
  "Updates the X - y coords of a storypoint"
  (print constructor))







