(ns story-planner.server.services.database.projects
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId))

(defn get-project [id userId]
  (let [projects (mc/find-maps db "projects" {$and [{:_id (ObjectId. id)}
                                                    {$or [{:userId userId}
                                                          {:authorizedUsers {$in [(str userId)]}}]}]})]
    (map ; Turn characters into a modified list
      ; #(comp (update % :_id str) (update % :userId str)) ; By updating each map :id by casting to a string
      #(conj % {:_id (str (:_id %))  :userId (str (:userId %))})
      projects)))

(defn get-projects [userId]
  (let [projects (mc/find-maps db "projects" {$or [{:userId userId}
                                                   {:authorizedUsers {$in [(str userId)]}}]})]
    (map ; Turn characters into a modified list
      #(update % :_id str) ; By updating each map :id by casting to a string
      projects)))

(defn check-project-permissions [projectId userId]
  "Checks that a project is owned by the user")
  ;TODO also check ifthe user has been granted access

(defn create-project [projectData]
  "insers a new project for current user"
  (let [new-project (mc/insert-and-return db "projects" (conj projectData {:boards [{:name "Base" :folder "n/a"}]}))]
    (get-project (str (:_id new-project)) (:userId projectData))))

(defn delete-project [projectData]
  "TODO test deleting from a non-auth user"
  (mc/remove db "projects" { :_id (ObjectId. (:id projectData))})
  (:id projectData))
; {:_id (ObjectId. (:projectId entityData))}
(defn create-entity [entityData userId]
  "Inserts an enttiy into the given folder or a root entities object"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId entityData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$push {:entities {:folder (:folder entityData) :title (:title entityData) :values (:value entityData) :id (str (ObjectId.)) :image (:image entityData)}}} {:upsert true}))]

    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:projectId entityData) userId))
      (response-handler/send-auth-error))))


(defn edit-entity [entityData userId]
  "We'll use a separate function here
  cause it would just be easier to separate them"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId entityData))}
                                                             {:entities {$elemMatch {:id (:entityId entityData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$set {"entities.$.title" (:title entityData)
                                                             "entities.$.values" (:value entityData)
                                                             "entities.$.image" (:image entityData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:projectId entityData) userId))
      (response-handler/send-auth-error))))


(defn create-folder [folderData userId]
  "Inserts a new folder"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id folderData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$push {:folders (dissoc folderData :id)}} {:upsert true}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id folderData) userId))
      (response-handler/send-auth-error))))



; TODO might want to look at rolling `create-board` and `create-entity` together - lot of redundency
(defn create-board [boardData userId]
  "Inserts an enttiy into the given folder or a root entities object"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId boardData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$push {:boards (:value boardData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:projectId boardData) userId))
      (response-handler/send-auth-error))))


; TODO probably need to roll this out into it's own attr - modifying story points is going to run into issues here
(defn create-storypoint [storyData userId]
  "Creates a blank story point for the :board :projectId combo"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId storyData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
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
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$set {"storypoints.$.position" (:position storyData) "storypoints.$.size" (:size storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

;TODO DRY
(defn update-storypoint-title [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$set {"storypoints.$.name" (:value storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

(defn update-storypoint-description [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                               {$set {"storypoints.$.description" (:value storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

(defn add-link-to-storypoint [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
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
                                                                         {:authorizedUsers {$in [(str userId)]}}]}]}
                                                         :u {"$set" {"storypoints.$.links.$[link].label" (:label storyData)}} :arrayFilters [ {"link.linkId" (:linkId storyData)}]}])))]
    (if projectUpdate
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))

(defn delete-storypoint [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$pull {"storypoints" {:id (:storypointId storyData)}}} true))]
    (if (> projectUpdate 0)
      (response-handler/wrap-response "project" (get-project (:id storyData) userId))
      (response-handler/send-auth-error))))
