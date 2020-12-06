(ns story-planner.server.services.database.media
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId))

(def base-media-object
  {:images []
   :folders []})

(defn create-base-media [user-id]
  (mc/insert-and-return db "media" (conj base-media-object {:owner user-id})))

(defn add-media [user-id url folder]
  (mc/update db "media" {:owner user-id} {$push {:images {:url url :folder folder}}}))
  ; (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:projectId boardData))}
  ;                                                            {$or [{:userId userId}
  ;                                                                  {:authorizedUsers {$in [(str userId)]}}]}]}
  ;                                                     {$push {:boards (:value boardData)}}))]))
