(ns story-planner.server.services.socket
  (:require
    [immutant.web.async       :as async]
    [cheshire.core            :refer :all]
    [story-planner.server.services.database.authorized :as DB-auth-users]
    [story-planner.server.services.database.projects :as DB-projects]
    [story-planner.server.services.database.folders :as DB-folders]
    [story-planner.server.services.database.entities :as DB-entities]
    [story-planner.server.services.database.storypoints :as DB-storypoints]
    [story-planner.server.services.amazon :as AWS]
    [story-planner.server.services.database.media :as media]))


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
  {:type "new-project" :msg-type "single" :data (DB-projects/create-project {:name (:value data) :userId (:_id (:user data))})})
(defmethod handle-websocket-message "delete-project"
  [data]
  {:type "delete-project" :msg-type "single" :data (DB-projects/delete-project {:id (:value data) :userId (:_id (:user data))})})
(defmethod handle-websocket-message "create-folder"
  [data]
  (DB-folders/create-folder {:name (:value data) :type (:folder data) :id (:projectId data)} (:_id (:user data))))
(defmethod handle-websocket-message "create-entity"
  [data]
  (DB-entities/create-entity (dissoc data :channel) (:_id (:user data))))
(defmethod handle-websocket-message "edit-entity"
  [data]
  (DB-entities/edit-entity (dissoc data :channel) (:_id (:user data))))
(defmethod handle-websocket-message "delete-entity"
  [data]
  (DB-entities/delete-entity (dissoc data :channel) (:_id (:user data))))
(defmethod handle-websocket-message "create-board"
  [data]
  (DB-projects/create-board (dissoc data :channel) (:_id (:user data))))
(defmethod handle-websocket-message "get-projects"
  [data] ; Returns the name and ID of all projects
  {:type "projects" :data (construct-all-project-return (DB-projects/get-projects (:_id (:user data))))})
(defmethod handle-websocket-message "get-project"
  [data] ; Returns the name and ID of all projects
  {:type "project-first"
   :data (DB-projects/get-project (:value data) (:_id (:user data)))})


(defmethod handle-websocket-message "get-images"
  [data]
  {:type "get-images"
   :data (media/load-media (:_id (:user data)))})

; Storypoint Handlers
(defmethod handle-websocket-message "create-storypoint"
  [data]
  (DB-storypoints/create-storypoint (dissoc data :channel) (:_id (:user data))))
(defmethod handle-websocket-message "update-storypoint-position"
  [data] ; Returns the name and ID of all projects
  (DB-storypoints/update-storypoint-position {:storypointId (:storypointId data) :position (:position data) :size (:size data) :id (:projectId data)} (:_id (:user data))))
(defmethod handle-websocket-message "update-storypoint-title"
  [data] ; Returns the name and ID of all projects
  (DB-storypoints/update-storypoint-title {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)} (:_id (:user data))))
(defmethod handle-websocket-message "update-storypoint-description"
  [data] ; Returns the name and ID of all projects
  (DB-storypoints/update-storypoint-description {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)} (:_id (:user data))))
(defmethod handle-websocket-message "update-storypoint-image"
  [data]
  (DB-storypoints/update-storypoint-image {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)} (:_id (:user data))))
(defmethod handle-websocket-message "delete-storypoint"
  [data]
  (DB-storypoints/delete-storypoint {:id (:projectId data) :storypointId (:storypointId data)} (:_id (:user data))))


(defmethod handle-websocket-message "add-link-to-storypoint"
  [data]
  (DB-projects/add-link-to-storypoint {:id (:projectId data) :storypointId (:storypointId data) :value (:value data)} (:_id (:user data))))
(defmethod handle-websocket-message "update-link-label"
  [data]
  (DB-projects/update-link-label {:id (:projectId data) :storypointId (:storypointId data) :linkId (:linkId data) :label (:label data)} (:_id (:user data))))
(defmethod handle-websocket-message "delete-link"
  [data]
  (DB-projects/delete-link {:id (:projectId data) :storypointId (:storypointId data) :linkId (:linkId data)} (:_id (:user data))))
(defmethod handle-websocket-message :default [data]
  (async/send! (:channel data) (generate-string "No method signiture found"))) ; String for consistency sake

; Authorized user flow methods
(defmethod handle-websocket-message "get-authorized-users"
  [data]
  {:type "get-authorized-users"
   :data (DB-auth-users/get-authorized-users (:_id (:user data)))})
(defmethod handle-websocket-message "add-new-authorized-user"
  [data]
  (DB-auth-users/add-authorized-user (:newUser data) (:projectIds data) (:_id (:user data)))
  {:type "get-authorized-users"
   :data (DB-auth-users/get-authorized-users (:_id (:user data)))})

(defmethod handle-websocket-message "delete-authorized-user"
  [data]
  (DB-auth-users/delete-authorized-user (:userId data) (:_id (:user data)))
  {:type "get-authorized-users"
   :data (DB-auth-users/get-authorized-users (:_id (:user data)))})

(defmethod handle-websocket-message "update-project-permissions"
  [data]
  (DB-auth-users/update-project-permissions (:_id (:user data)) (:authorizedUsers data) (:projectId data))
  {:type "generic"
   :data "success"})



