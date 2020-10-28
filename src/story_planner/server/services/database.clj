(ns story-planner.server.services.database
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [mount.core :refer [defstate]]
           [config.core :refer [env]]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId))

(declare get-project)
(declare get-projects)

(defstate db*
  :start (-> env :database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

; (let [conn (mg/connect {:host "db.megacorp.internal" :port 7878})])

(defn get-document-by-type [])
(defn get-all-documents-for-user [])

; Need to add our CRUD stuff here



; TODO move this to it's own file

(defn add-user [user]
  "checks should be done prior to this point for anything we need to do"
  ; This token generation is probably good enough given Java's implementation of UUID is suppose to be secure
  (dissoc (update (mc/insert-and-return db "users" (conj user {:token (str (java.util.UUID/randomUUID))})) :_id str) :password))

(defn get-user [email]
  (mc/find-maps db "users" {:email email}))

(defn update-user-token [email]
  (let [token (str (java.util.UUID/randomUUID))]
    (mc/update db "users" {:email email} {$set {:token token }} {:upsert true})
    token))

(defn get-user-by-token [token]
  (let [user (mc/find-maps db "users" {:token token})]
    (if (> (count user) 0)
      (first user)
      false)))

(defn check-user-token [token]
  (let [user (mc/find-maps db "users" {:token token})]
    (if (> (count user) 0)
      true
      false)))

(defn add-user-stripe-token [sub-token user-token]
  (mc/update db "users" {:token user-token} {$set {:subToken sub-token }} {:upsert true})
  sub-token)

; TODO probably best to keep this all under the project as one big entity
; TODO move these
;CREATE METHODS

(defn check-project-permissions [projectId userId]
  "Checks that a project is owned by the user")
  ;TODO also check ifthe user has been granted access

(defn create-project [projectData]
  "insers a new project for current user"
  (let [new-project (mc/insert-and-return db "projects" projectData)]
    (get-project (str (:_id new-project)))))

(defn delete-project [projectData]
  "TODO test deleting from a non-auth user"
  (mc/remove db "projects" { :_id (ObjectId. (:id projectData))})
  (:id projectData))
; {:_id (ObjectId. (:projectId entityData))}
(defn create-entity [entityData userId]
  "Inserts an enttiy into the given folder or a root entities object"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId entityData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                                                      {$push {:entities {:folder (:folder entityData) :title (:title entityData) :values (:value entityData) :id (str (ObjectId.)) :image (:image entityData)}}} {:upsert true}))]

    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:projectId entityData) userId))
      (response-handler/send-auth-error))))


(defn edit-entity [entityData]
  "We'll use a separate function here
  cause it would just be easier to separate them")

(defn create-folder [folderData userId]
  "Inserts a new folder"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id folderData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                                                      {$push {:folders (dissoc folderData :id)}} {:upsert true}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id folderData) userId))
      (response-handler/send-auth-error))))



; TODO might want to look at rolling `create-board` and `create-entity` together - lot of redundency
(defn create-board [boardData userId]
  "Inserts an enttiy into the given folder or a root entities object"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId boardData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                                                      {$push {:boards (:value boardData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:projectId boardData) userId))
      (response-handler/send-auth-error))))


; TODO probably need to roll this out into it's own attr - modifying story points is going to run into issues here
(defn create-storypoint [storyData userId]
  "Creates a blank story point for the :board :projectId combo"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId storyData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                                                      {$push {"storypoints" {
                                                                             :name "Title"
                                                                             :id (str (ObjectId.))
                                                                             :board (:board storyData)
                                                                             :description "Description"
                                                                             :entityId (:entityId storyData)
                                                                             :position (:position storyData)
                                                                             :size (:size storyData)}}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:projectId storyData) userId))
      (response-handler/send-auth-error))))

; Choice to break up edits to prevent race conditions if multiple users edit same storypoint
(defn update-storypoint-position [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                                                      {$set {"storypoints.$.position" (:position storyData) "storypoints.$.size" (:size storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

;TODO DRY
(defn update-storypoint-title [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                                                      {$set {"storypoints.$.name" (:value storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

(defn update-storypoint-description [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                               {$set {"storypoints.$.description" (:value storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

(defn add-link-to-storypoint [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                                                      {$push {"storypoints.$.links" {:id (:value storyData) :linkId (str (ObjectId.))}}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

(defn update-link-label [storyData userId]
  "This one is a bit different since the command results is different - we have to use that as the update filters we used
    aren't supported by the core monger - have to access it with a manual mongo command"
  (let [projectUpdate (.ok (mg/command db (sorted-map :update "projects"
                                              :updates [{:q {$and [{:_id (ObjectId. (:id storyData))}
                                                                   {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                                   {$or [{:userId userId}
                                                                         {:authorizedUsers {$in [userId]}}]}]}
                                                         :u {"$set" {"storypoints.$.links.$[link].label" (:label storyData)}} :arrayFilters [ {"link.linkId" (:linkId storyData)}]}])))]
    (if projectUpdate
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

(defn delete-storypoint [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [userId]}}]}]}
                                                      {$pull {"storypoints" {:id (:storypointId storyData)}}} true))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))


;TODO move this to its own file
(defn get-authorized-users [userId]
  (let [users (mc/find-maps db "users" {:parentId userId})]
    (map ; Turn characters into a modified list
      ; #(comp (update % :_id str) (update % :userId str)) ; By updating each map :id by casting to a string
      #(conj % {:_id (str (:_id %))  :parentId (str (:parentId %))})
      users)))

(defn add-new-user-project [authorizedUserId parentId projectIds]
  (let [projectList (get-projects parentId)]
    (doseq [projectId projectIds]
      (let [project (first
                     (filter #(= projectId (:_id %)) projectList))]
        (if project
          (mc/update db "projects" {:_id (ObjectId. (:_id project))}
                                   {$push {"authorizedUsers" authorizedUserId}} {:upsert true}))))))

(defn add-authorized-user [user projectIds parentId]
  (let [newUser (mc/insert-and-return db "users"
                  (conj
                    user
                   {:token ""
                    :parentId parentId
                    :type "sub"
                    :setupToken (str (java.util.UUID/randomUUID))}))]
    (add-new-user-project (str (:_id newUser)) parentId projectIds)))

(defn update-project-permissions [userId authorizedUsers projectId]
  (mc/update db "projects" {:_id (ObjectId. projectId)}
                           {$set {"authorizedUsers" authorizedUsers}} {:upsert true}))

(defn user-with-token-exists? [token]
  (> (count (mc/find-maps db "users" {:setupToken token})) 0))

(defn update-auth-user [token password]
  (let [loginToken (str (java.util.UUID/randomUUID))]
    (mc/update db "users" {:setupToken token}
                         {$set {"setupToken" nil "password" password "token" loginToken}} {:upsert true})
    (dissoc (get-user-by-token loginToken) :password :parentId :id)))



; (wrap-response "success" (dissoc (conj (first user) {:token (DB/update-user-token (:email user-creds))}) :_id :password))
; READ METHODS
; TODO remove let - can simplify a bit
(defn get-project [id userId]
  (let [projects (mc/find-maps db "projects" {:_id (ObjectId. id) :userId userId})]
    (map ; Turn characters into a modified list
      ; #(comp (update % :_id str) (update % :userId str)) ; By updating each map :id by casting to a string
      #(dissoc (conj % {:_id (str (:_id %))  :userId (str (:userId %))}) :authorizedUsers)
      projects)))

(defn get-projects [userId]
  (let [projects (mc/find-maps db "projects" {:userId userId})]
    (map ; Turn characters into a modified list
      #(update % :_id str) ; By updating each map :id by casting to a string
      projects)))
