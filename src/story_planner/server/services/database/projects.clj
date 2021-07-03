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
  (let [new-project (mc/insert-and-return db "projects" (conj projectData {:boards [{:name "Base" :folder "n/a" :id (str (ObjectId.))}]}))]
    (get-project (str (:_id new-project)) (:userId projectData))))

(defn delete-project [projectData]
  "TODO test deleting from a non-auth user"
  (mc/remove db "projects" { :_id (ObjectId. (:id projectData))})
  (:id projectData))


; TODO might want to look at rolling `create-board` and `create-entity` together - lot of redundency
(defn create-board [boardData userId]
  "Inserts an enttiy into the given folder or a root entities object"
  (let [id (str (ObjectId.))
        projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId boardData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$push {:boards (conj (:value boardData) {:id id})}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "new-board" "all" (dissoc (update-in boardData [:value] conj {:id id}) :user :token))
      (response-handler/send-auth-error))))

(defn delete-board [boardData userId]
  ; TODO delete all storypoints assocaited with board
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId boardData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$pull {"boards" {:id (:id boardData)}}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "delete-board" "all" {:id (:id boardData) :projectId (:projectId boardData)})
      (response-handler/send-auth-error))))

(defn edit-board-name [boardData userId]
  "Edits a board name"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId boardData))}
                                                             {:boards {$elemMatch {:id (:id boardData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$set {"boards.$.name" (:name boardData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "edit-board-name" "all" (select-keys boardData [:projectId :id :name]))
      (response-handler/send-auth-error))))


