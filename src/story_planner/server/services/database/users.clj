(ns story-planner.server.services.database.users
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.database.media :as media]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId))


(defn add-user [user]
  "checks should be done prior to this point for anything we need to do"
  ; This token generation is probably good enough given Java's implementation of UUID is suppose to be secure
  (let [user-token (str (java.util.UUID/randomUUID))
        new-user (mc/insert-and-return db "users" (conj user {:token user-token}))]
    (media/create-base-media (:_id new-user))
    (dissoc (update new-user :_id str) :password)))

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