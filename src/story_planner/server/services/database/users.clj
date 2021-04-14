(ns story-planner.server.services.database.users
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [buddy.hashers :as hashers]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.database.media :as media]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId)
  (:import [java.security SecureRandom] java.util.Base64))

(defn generate-access-token []
  "Generates a secure random string to use as an access token"
  (let [random (SecureRandom/getInstance "SHA1PRNG")
        buffer (make-array Byte/TYPE 32)]
    (.nextBytes random buffer)
    (.encodeToString (Base64/getEncoder) buffer)))

(defn is-token-valid? [token hashed-token]
  (:valid (hashers/verify token hashed-token)))

(defn add-user [user]
  "checks should be done prior to this point for anything we need to do"
  ; This token generation is probably good enough given Java's implementation of UUID is suppose to be secure
  (let [user-token (str (generate-access-token))
        new-user (mc/insert-and-return db "users" (conj user {:token (hashers/derive user-token {:alg :bcrypt+sha512})}))]
    (media/create-base-media (:_id new-user))
    (conj (dissoc (update new-user :_id str) :password) {:token user-token})))

(defn get-user [email]
  (mc/find-maps db "users" {:email email}))

(defn update-user-token [email]
  (let [token (str (hashers/derive user-token {:alg :bcrypt+sha512}))]
    (mc/update db "users" {:email email} {$set {:token token }} {:upsert true})
    token))

(defn get-user-by-token [id token]
  "returns a user associated with a token, otherwise returns false"
  (let [user (mc/find-one-as-map db "users" {:_id (ObjectId. id)})]
    (if (and user (is-token-valid? token (:token user)))
      user
      false)))


(defn check-user-token [id token]
  "checks if a token is a valid token"
  (let [user (mc/find-one-as-map db "users" {:_id (ObjectId. id)})]
    (if (and user (is-token-valid? token (:token user)))
      true
      false)))

(defn add-user-stripe-token [sub-token user-token]
  (mc/update db "users" {:token user-token} {$set {:subToken sub-token }} {:upsert true})
  sub-token)

(defn add-user-media-folder [token folder-name]
  (let [user (get-user-by-token token)]
    (media/add-media-folder (:_id user) folder-name)))

(defn remove-image [token url]
  (let [user (get-user-by-token token)]
    (media/remove-image (:_id user) url)))

