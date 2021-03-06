(ns story-planner.server.services.database.media
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           ; [story-planner.server.services.database.users :as DB-users]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId))

(def base-media-object
  {:images []
   :folders []})

(defn create-base-media [user-id]
  (mc/insert-and-return db "media" (conj base-media-object {:owner user-id})))

(defn add-media [user-id url folder]
  (let [media-object {:url url :folder folder}]
    (mc/update db "media" {:owner (ObjectId. user-id)} {$push {:images media-object}})
    media-object))

(defn add-media-folder [user-id folder]
  (mc/update db "media" {:owner (ObjectId. user-id)} {$push {:folders folder}})
  folder)

(defn load-media [user-id]
  (let [media (mc/find-one-as-map db "media" {:owner (ObjectId. user-id)})]
    (update (update media :_id str) :owner str))) ; By updating each map :id by casting to a string

(defn remove-image [user-id url]
  (let [imageUpdate (.getN (mc/update db "media" {:owner (ObjectId. user-id)} {$pull {:images {:url url}}}))]
    (if (> imageUpdate 0)
      url
      "Handle Failure")))
