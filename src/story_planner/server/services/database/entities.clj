(ns story-planner.server.services.database.entities
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.response-handler :as response-handler]
           [story-planner.server.services.database.projects :refer [get-project]])
  (:import org.bson.types.ObjectId))




(defn create-entity [entityData userId]
  "Inserts an enttiy into the given folder or a root entities object"
  (let [id (str (ObjectId.))
        projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId entityData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$push {:entities {:folder (:folder entityData) :title (:title entityData) :values (:value entityData) :id id :image (:image entityData)}}} {:upsert true}))]

    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "new-entity" "all" (dissoc (conj entityData {:id id}) :user))
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
      (response-handler/wrap-ws-response "edit-entity" "all" (dissoc entityData :user :_id))
      (response-handler/send-auth-error))))

(defn delete-entity [entityData userId]
  "Removes an entity from a project"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId entityData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$pull {"entities" {:id (:entityId entityData)}}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "delete-entity" "all" {:id (:entityId entityData) :projectId (:projectId entityData)})
      (response-handler/send-auth-error))))
