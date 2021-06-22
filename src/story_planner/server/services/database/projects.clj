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
