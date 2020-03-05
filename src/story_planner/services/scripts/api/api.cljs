(ns story-planner.services.scripts.api.api
  (:require [story-planner.services.scripts.api.websocket :refer [send-message]]
            [story-planner.services.state.global :refer [get-from-state]]))

; CLIENT SIDE API REQUESTS
; This file only handles the actual requsts to the server that the application makes
; return and state updating functions take place in the WS connection



(defn create-project [constructor]
  (send-message {:type "create-project" :value constructor}))
(defn delete-project [])
(defn edit-project [])
(defn get-project [id]
  (print id)
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
  (send-message {:type "update-storypoint-position"
                 :projectId (:_id (get-from-state "currentProject"))
                 :storypointId (:id constructor)
                 :position {:x (:x constructor ) :y (:y constructor)}}))

(defn update-storypoint-title [constructor]
  "updates the title property"
  (send-message {:type "update-storypoint-title"
                 :projectId (:_id (get-from-state "currentProject"))
                 :storypointId (:id constructor)
                 :value (:value constructor)}))

(defn update-storypoint-description [constructor]
  "updates the description property"
  (send-message {:type "update-storypoint-description"
                 :projectId (:_id (get-from-state "currentProject"))
                 :storypointId (:id constructor)
                 :value (:value constructor)}))

(defn add-link-to-storypoint [constructor]
  "adds a link to the storypoint"
  (send-message {:type "add-link-to-storypoint"
                 :projectId (:_id (get-from-state "currentProject"))
                 :storypointId (:storypointId constructor)
                 :value (:value constructor)}))

(defn delete-storypoint [constructor]
  "removes a storypoint from a project"
  (send-message {:type "delete-storypoint"
                 :projectId (:_id (get-from-state "currentProject"))
                 :storypointId (:storypointId constructor)}))







