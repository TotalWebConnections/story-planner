(ns story-planner.server.services.database
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.operators :refer :all]
           [mount.core :refer [defstate]]
           [config.core :refer [env]])
  (:import org.bson.types.ObjectId))

(declare get-project)

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
  (:token (mc/insert-and-return db "users" (conj user {:token (str (java.util.UUID/randomUUID))}))))

(defn get-user [email]
  (mc/find-maps db "users" {:email email}))

(defn update-user-token [email]
  (let [token (str (java.util.UUID/randomUUID))]
    (mc/update db "users" {:email email} {$set {:token token }} {:upsert true})
    token))

; TODO probably best to keep this all under the project as one big entity
; TODO move these
;CREATE METHODS

;TODO when we create a folder need to append it to the folders on the
; (:projectId folderData)
(defn create-folder [folderData]
  "Inserts a new folder"
  (mc/update db "projects" {:_id (ObjectId. (:id folderData))}
    {$push {:folders (dissoc folderData :id)}} {:upsert true})
  (get-project (:id folderData)))

(defn create-project [projectData]
  "insers a new project for current user"
  (let [new-project (mc/insert-and-return db "projects" projectData)]
    (get-project (str (:_id new-project)))))

(defn create-entity [entityData]
  "Inserts an enttiy into the given folder or a root entities object"
  (mc/update db "projects" {:_id (ObjectId. (:projectId entityData))}
    {$push {:entities {:folder (:folder entityData) :title (:title entityData) :values (:value entityData)}}} {:upsert true})
  (get-project (:projectId entityData))) ; TODO handle save to specific folder path

(defn edit-entity [entityData]
  "We'll use a separate function here
  cause it would just be easier to separate them")


; TODO might want to look at rolling `create-board` and `create-entity` together - lot of redundency
(defn create-board [boardData]
  "Inserts an enttiy into the given folder or a root entities object"
  (mc/update db "projects" {:_id (ObjectId. (:projectId boardData))}
    {$push {:boards (:value boardData)}} {:upsert true})
  (get-project (:projectId boardData)))


; TODO probably need to roll this out into it's own attr - modifying story points is going to run into issues here
(defn create-storypoint [storyData]
  "Creates a blank story point for the :board :projectId combo"
  (mc/update db "projects" {:_id (ObjectId. (:projectId storyData))}
    {$push {"storypoints" {
                           :name "Title"
                           :id (str (ObjectId.))
                           :board (:board storyData)
                           :description "Description"
                           :position (:position storyData)
                           :size (:size storyData)}}})
  (get-project (:projectId storyData))) ; TODO we can define this type elsewhere for reuse

; Choice to break up edits to prevent race conditions if multiple users edit same storypoint
(defn update-storypoint-position [storyData]
  (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                  {:storypoints {$elemMatch {:id (:storypointId storyData)}}}]}
    {$set {"storypoints.$.position" (:position storyData) "storypoints.$.size" (:size storyData)}})
  (get-project (:id storyData)))

;TODO DRY
(defn update-storypoint-title [storyData]
  (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                  {:storypoints {$elemMatch {:id (:storypointId storyData)}}}]}
    {$set {"storypoints.$.name" (:value storyData)}})
  (get-project (:id storyData)))

(defn update-storypoint-description [storyData]
  (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                  {:storypoints {$elemMatch {:id (:storypointId storyData)}}}]}
    {$set {"storypoints.$.description" (:value storyData)}})
  (get-project (:id storyData)))

(defn add-link-to-storypoint [storyData]
  (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                  {:storypoints {$elemMatch {:id (:storypointId storyData)}}}]}
    {$push {"storypoints.$.links" {:id (:value storyData) :linkId (str (ObjectId.))}}})
  (get-project (:id storyData)))

(defn update-link-label [storyData]
  (mg/command db (sorted-map :update "projects"
                   :updates [{:q {$and [{:_id (ObjectId. (:id storyData))}
                                        {:storypoints {$elemMatch {:id (:storypointId storyData)}}}]}
                              :u {"$set" {"storypoints.$.links.$[link].label" (:label storyData)}} :arrayFilters [ {"link.linkId" (:linkId storyData)}]}]))
  (get-project (:id storyData)))

(defn delete-storypoint [storyData]
  (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                  {:storypoints {$elemMatch {:id (:storypointId storyData)}}}]}
    {$pull {"storypoints" {:id (:storypointId storyData)}}} true)
  (get-project (:id storyData)))

; READ METHODS
; TODO remove let - can simplify a bit
(defn get-project [id]
  (let [projects (mc/find-maps db "projects" {:_id (ObjectId. id)})]
    (map ; Turn characters into a modified list
      #(update % :_id str) ; By updating each map :id by casting to a string
      projects)))

(defn get-projects [userId]
  (let [projects (mc/find-maps db "projects" {:userId userId})]
    (map ; Turn characters into a modified list
      #(update % :_id str) ; By updating each map :id by casting to a string
      projects)))
