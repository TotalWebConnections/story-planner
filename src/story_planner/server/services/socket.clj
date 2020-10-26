(ns story-planner.server.services.socket
  (:require
    [immutant.web.async       :as async]
    [cheshire.core            :refer :all]
    [story-planner.server.services.database :as DB]
    [story-planner.server.services.amazon :as AWS]))


(defn construct-all-project-return [query]
  "pulls out the ids to send for the all projects page"
  (map (fn [project]
        {:_id (:_id project)
          :name (:name project)
          :authorizedUsers (map #(str %) (:authorizedUsers project))} ) query))


; Handlers for our websocket functions
(defmulti handle-websocket-message (fn [data] (:type data)))
(defmethod handle-websocket-message "create-project"
  [data]
  (generate-string
    {:type "new-project" :data (DB/create-project {:name (:value data) :userId (:_id (:user data))})}))
(defmethod handle-websocket-message "delete-project"
  [data]
  (generate-string
    {:type "delete-project" :data (DB/delete-project {:id (:value data) :userId (:_id (:user data))})}))
(defmethod handle-websocket-message "create-folder"
  [data]
  (generate-string (DB/create-folder {:name (:value data) :type (:folder data) :id (:projectId data)} (:_id (:user data)))))
(defmethod handle-websocket-message "create-entity"
  [data]
  (generate-string (DB/create-entity (dissoc data :channel) (:_id (:user data)))))
(defmethod handle-websocket-message "create-board"
  [data]
  (generate-string (DB/create-board (dissoc data :channel) (:_id (:user data)))))
(defmethod handle-websocket-message "create-storypoint"
  [data]
  (generate-string (DB/create-storypoint (dissoc data :channel) (:_id (:user data)))))
(defmethod handle-websocket-message "get-projects"
  [data] ; Returns the name and ID of all projects
  (generate-string
    {:type "projects" :data (construct-all-project-return (DB/get-projects (:_id (:user data))))}))
(defmethod handle-websocket-message "get-project"
  [data] ; Returns the name and ID of all projects
  (generate-string
    {:type "project-first"
     :data [(DB/get-project (:value data) (:_id (:user data)))
            (map (fn [img] {:src (:key img)}) (:object-summaries (AWS/handle-load-images (:_id (:user data)))))]}))
(defmethod handle-websocket-message "update-storypoint-position"
  [data] ; Returns the name and ID of all projects
  (generate-string (DB/update-storypoint-position {:storypointId (:storypointId data) :position (:position data) :size (:size data) :id (:projectId data)} (:_id (:user data)))))
(defmethod handle-websocket-message "update-storypoint-title"
  [data] ; Returns the name and ID of all projects
  (generate-string (DB/update-storypoint-title {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)} (:_id (:user data)))))
(defmethod handle-websocket-message "update-storypoint-description"
  [data] ; Returns the name and ID of all projects
  (generate-string (DB/update-storypoint-description {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)} (:_id (:user data)))))
(defmethod handle-websocket-message "add-link-to-storypoint"
  [data]
  (generate-string (DB/add-link-to-storypoint {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)} (:_id (:user data)))))
(defmethod handle-websocket-message "update-link-label"
  [data]
  (generate-string (DB/update-link-label {:id (:projectId data) :storypointId (:storypointId data) :linkId (:linkId data) :label (:label data)} (:_id (:user data)))))
(defmethod handle-websocket-message "delete-storypoint"
  [data]
  (generate-string (DB/delete-storypoint {:id (:projectId data) :storypointId (:storypointId data)} (:_id (:user data)))))
(defmethod handle-websocket-message :default [data]
  (async/send! (:channel data) (generate-string "No method signiture found"))) ; String for consistency sake

; Authorized user flow methods
(defmethod handle-websocket-message "get-authorized-users"
  [data]
  (generate-string
    {:type "get-authorized-users"
     :data (DB/get-authorized-users (:_id (:user data)))}))
(defmethod handle-websocket-message "add-new-authorized-user"
  [data]
  (DB/add-authorized-user (:newUser data) (:projectIds data) (:_id (:user data))))
; (defmethod handle-websocket-message "update-user-permissions"
;   "Takes a users permissions and overrides them with new ones"
;   [data])



