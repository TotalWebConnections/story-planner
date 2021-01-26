(ns story-planner.server.services.database.authorized
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.database.users :as DB-users]
           [story-planner.server.services.database.projects :as DB-projects]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId))



(defn get-authorized-users [userId]
  (let [users (mc/find-maps db "users" {:parentId userId})]
    (map ; Turn characters into a modified list
      ; #(comp (update % :_id str) (update % :userId str)) ; By updating each map :id by casting to a string
      #(conj % {:_id (str (:_id %))  :parentId (str (:parentId %))})
      users)))

(defn add-new-user-project [authorizedUserId parentId projectIds]
  (let [projectList (DB-projects/get-projects parentId)]
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

(defn delete-authorized-user [userId parentId]
  (mc/remove db "users" {$and [{:_id (ObjectId. userId)} {:parentId parentId}]}))

(defn update-project-permissions [userId authorizedUsers projectId]
  (mc/update db "projects" {:_id (ObjectId. projectId)}
                           {$set {"authorizedUsers" authorizedUsers}} {:upsert true}))

(defn user-with-token-exists? [token]
  (> (count (mc/find-maps db "users" {:setupToken token})) 0))

(defn update-auth-user [token password]
  (let [loginToken (str (java.util.UUID/randomUUID))]
    (mc/update db "users" {:setupToken token}
                         {$set {"setupToken" nil "password" password "token" loginToken}} {:upsert true})
    (dissoc (DB-users/get-user-by-token loginToken) :password :parentId :id)))
