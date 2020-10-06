(ns story-planner.server.services.socket
  (:require
    [immutant.web.async       :as async]
    [cheshire.core            :refer :all]
    [story-planner.server.services.database :as DB]))


(defn construct-all-project-return [query]
  "pulls out the ids to send for the all projects page"
  (map (fn [project]
        {:_id (:_id project) :name (:name project)} ) query))


; Handlers for our websocket functions
(defmulti handle-websocket-message (fn [data] (:type data)))
(defmethod handle-websocket-message "create-project"
  [data]
  (generate-string
    {:type "new-project" :data (DB/create-project {:name (:value data) :userId "123"})}))
(defmethod handle-websocket-message "delete-project"
  [data]
  (generate-string
    {:type "delete-project" :data (DB/delete-project {:id (:value data) :userId "123"})}))
(defmethod handle-websocket-message "create-folder"
  [data]
  (generate-string
    {:type "project" :data (DB/create-folder {:name (:value data) :type (:folder data) :id (:projectId data)})}))
(defmethod handle-websocket-message "create-entity"
  [data]
  (generate-string
    {:type "project" :data (DB/create-entity (dissoc data :channel))}))
(defmethod handle-websocket-message "create-board"
  [data]
  (generate-string
    {:type "project" :data (DB/create-board (dissoc data :channel))}))
(defmethod handle-websocket-message "create-storypoint"
  [data]
  (generate-string
    {:type "project" :data (DB/create-storypoint (dissoc data :channel))}))
(defmethod handle-websocket-message "get-projects"
  [data] ; Returns the name and ID of all projects
  (generate-string
    {:type "projects" :data (construct-all-project-return (DB/get-projects (:value data)))}))
(defmethod handle-websocket-message "get-project"
  [data] ; Returns the name and ID of all projects
  (generate-string
    {:type "project" :data (DB/get-project (:value data))}))
(defmethod handle-websocket-message "update-storypoint-position"
  [data] ; Returns the name and ID of all projects
  (generate-string
    {:type "project" :data (DB/update-storypoint-position {:storypointId (:storypointId data) :position (:position data) :size (:size data) :id (:projectId data)})}))
(defmethod handle-websocket-message "update-storypoint-title"
  [data] ; Returns the name and ID of all projects
  (generate-string
    {:type "project" :data (DB/update-storypoint-title {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)})}))
(defmethod handle-websocket-message "update-storypoint-description"
  [data] ; Returns the name and ID of all projects
  (generate-string
    {:type "project" :data (DB/update-storypoint-description {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)})}))
(defmethod handle-websocket-message "add-link-to-storypoint"
  [data]
  (generate-string
    {:type "project" :data (DB/add-link-to-storypoint {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)})}))
(defmethod handle-websocket-message "update-link-label"
  [data]
  (generate-string
    {:type "project" :data (DB/update-link-label {:id (:projectId data) :storypointId (:storypointId data) :linkId (:linkId data) :label (:label data)})}))
(defmethod handle-websocket-message "delete-storypoint"
  [data]
  (generate-string
    {:type "project" :data (DB/delete-storypoint {:id (:projectId data) :storypointId (:storypointId data)})}))
(defmethod handle-websocket-message :default [data]
  (async/send! (:channel data) (generate-string "No method signiture found"))) ; String for consistency sake



