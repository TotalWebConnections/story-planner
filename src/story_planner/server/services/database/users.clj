(ns story-planner.server.services.database.users
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [buddy.hashers :as hashers]
           [story-planner.server.services.database.hashers :refer [sha256]]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.database.media :as media]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId)
  (:import [java.security SecureRandom] java.util.Base64 java.security.MessageDigest))

(defn generate-access-token []
  "Generates a secure random string to use as an access token"
  (let [random (SecureRandom/getInstance "SHA1PRNG")
        buffer (make-array Byte/TYPE 128)]
    (.nextBytes random buffer)
    (.encodeToString (Base64/getEncoder) buffer)))

(defn is-token-valid? [token hashed-token]
  (= (sha256 token) hashed-token))

(defn add-user [user]
  "checks should be done prior to this point for anything we need to do"
  (let [user-token (str (generate-access-token))
        new-user (mc/insert-and-return db "users" (conj user {:token (sha256 user-token) :email (clojure.string/lower-case (:email user))}))]
    (media/create-base-media (:_id new-user))
    (conj (dissoc (update new-user :_id str) :password) {:token user-token})))

(defn get-user [email]
  "returns a user by email and false if no user - NOTE THIS IS PRE AUTH SO ANYONE CAN MAKE THIS REQUEST"
  (let [user (mc/find-one-as-map db "users" {:email (clojure.string/lower-case email)})]
    (if user
      (update user :_id str)
      false)))

(defn update-user-token [email]
  (let [token (str (generate-access-token))]
    (mc/update db "users" {:email (clojure.string/lower-case email)} {$set {:token (sha256 token) }} {:upsert true})
    token))

(defn get-user-by-token [id token]
  "returns a user associated with a token, otherwise returns false"
  (let [user (mc/find-one-as-map db "users" {:_id (ObjectId. id)})]
    (if (and user (is-token-valid? token (:token user)))
      (update user :_id str)
      false)))


(defn check-user-token [id token]
  "checks if a token is a valid token"
  (let [user (mc/find-one-as-map db "users" {:_id (ObjectId. id)})]
    (if (and user (is-token-valid? token (:token user)))
      true
      false)))

(defn add-user-stripe-token [sub-token id]
  (mc/update db "users" {:_id id} {$set {:subToken sub-token }} {:upsert true})
  sub-token)

(defn add-user-media-folder [id token folder-name]
  (let [user (get-user-by-token id token)]
    (media/add-media-folder (:_id user) folder-name)))

(defn remove-image [id token url]
  (let [user (get-user-by-token id token)]
    (media/remove-image (:_id user) url)))

