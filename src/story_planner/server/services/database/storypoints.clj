(ns story-planner.server.services.database.storypoints
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId))

(defn create-new-storypoint [storyData]
  {:name "Title"
   :id (str (ObjectId.))
   :board (:board storyData)
   :description "Description"
   :entityId (:entityId storyData)
   :position (:position storyData)
   :size (:size storyData)})

(defn create-storypoint [storyData userId]
  "Creates a blank story point for the :board :projectId combo"
  (let [storypoint (create-new-storypoint storyData)
        projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId storyData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$push {"storypoints" storypoint}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "new-storypoint" "all" (conj storypoint {:projectId (:projectId storyData)}))
      (response-handler/send-auth-error))))

; Choice to break up edits to prevent race conditions if multiple users edit same storypoint
(defn update-storypoint-position [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$set {"storypoints.$.position" (:position storyData) "storypoints.$.size" (:size storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "update-storypoint-position" "all" (select-keys storyData [:storypointId :position :size]))
      (response-handler/send-auth-error))))

(defn update-storypoint-title [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$set {"storypoints.$.name" (:value storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "update-storypoint-title" "all" (select-keys storyData [:storypointId :value]))
      (response-handler/send-auth-error))))

(defn update-storypoint-description [storyData userId]
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                               {$set {"storypoints.$.description" (:value storyData)}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "update-storypoint-description" "all" (select-keys storyData [:storypointId :value]))
      (response-handler/send-auth-error))))
