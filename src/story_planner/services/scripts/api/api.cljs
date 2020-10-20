(ns story-planner.services.scripts.api.api
  (:require [story-planner.services.scripts.api.websocket :refer [send-message]]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.services.scripts.trial :as trial-checks]))

; CLIENT SIDE API REQUESTS
; This file only handles the actual requsts to the server that the application makes
; return and state updating functions take place in the WS connection



(defn create-project [constructor]
  (send-message {:type "create-project" :value (:project constructor)}))
(defn delete-project [id]
  (send-message {:type "delete-project" :value id}))
(defn edit-project [])
(defn get-project [id]
  (send-message {:type "get-project" :value id}))
(defn get-projects [token]
  (send-message {:type "get-projects" :token token})) ; value here represents our userID!

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
  (if (trial-checks/user-able-to-add?)
    (send-message {:type "create-entity"
                   :folder (:folder constructor)
                   :projectId (:projectId constructor)
                   :value (:value constructor)
                   :title (:title constructor)
                   :image (:image constructor)})
    (js/alert "Max added - please subcribe for unlimited."))) ;TODO make nice
(defn delete-entity [])
(defn edit-entiy [])



(defn create-storypoint [constructor]
  "creates a brand new storpy point associated with the board/projectID combo"
  (if (trial-checks/user-able-to-add?)
    (send-message {:type "create-storypoint" :projectId (:projectId constructor)
                   :board (:board constructor)
                   :entityId (:entityId constructor)
                   :position (:position constructor)
                   :size (:size constructor)})
    (js/alert "Max added - please subcribe for unlimited."))) ;TODO make nice

; NOTE - opted to break apart updates here to prevent weird race conditions
; where someone edits the name and another moves it - these states could still happen
; but the odds are much less and likely to be two people trying to edit the same attr that
; causes teh condition
(defn update-storypoint-position [constructor]
  "Updates the X - y coords of a storypoint"
  (send-message {:type "update-storypoint-position"
                 :projectId (:_id (get-from-state "currentProject"))
                 :storypointId (:id constructor)
                 :position {:x (:x constructor ) :y (:y constructor)}
                 :size {:h (:height constructor) :w (:width constructor)}}))
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

(defn update-link-label [constructor]
  "updates a storypoint link's label"
  (send-message {:type "update-link-label"
                 :projectId (:_id (get-from-state "currentProject"))
                 :storypointId (:storypointId constructor)
                 :linkId (:linkId constructor)
                 :label (:label constructor)}))

(defn delete-storypoint [constructor]
  "removes a storypoint from a project"
  (send-message {:type "delete-storypoint"
                 :projectId (:_id (get-from-state "currentProject"))
                 :storypointId (:storypointId constructor)}))







