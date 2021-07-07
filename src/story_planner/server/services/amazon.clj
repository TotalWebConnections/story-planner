(ns story-planner.server.services.amazon
  (:require [config.core :refer [env]]
            [clojure.string :as str]
            [story-planner.server.services.database.users :as DB-users]
            [story-planner.server.services.database.media :as media])
  (:use [amazonica.aws.s3]))

(def MAX_UPLOAD_SIZE 10000000) ; 10mb to start

(defn is-image? [file]
  "makes sure the metadata has image in it or rejects"
  (str/includes? (:content-type file) "image"))

(defn is-image-under-limit [file]
  (< (:size file) MAX_UPLOAD_SIZE))


(defn handle-image-upload [file]
  (let [user (DB-users/get-user-by-token (get file "_id") (get file "token"))
        folder (get file "folder")]
    ; TODO we should make this better
    (if (is-image? (get file "myFile"))
      (if (is-image-under-limit (get file "myFile"))
        (if user
          (do
            (put-object (:s3creds env)
                  :bucket-name "story-planner"
                  :key (str (str (:_id user)) "/" (:filename (get file "myFile")))
                  :file (:tempfile (get file "myFile")))
            (media/add-media (:_id user) (str (str (:_id user)) "/" (:filename (get file "myFile"))) folder))
          "User Auth Failed")
        "Image Too Large")
      "Must Be An Image")))


(defn handle-load-images [id]
  "gets the list of all img urls for a given user"
  ;TODO take an array of ID to load for authed users
  (list-objects-v2 (:s3creds env)
                   {:bucket-name "story-planner"
                    :prefix (str id)}))

(defn handle-delete-image [file-name]
  "deletes an image for user"
  (delete-object (:s3creds env)
                :bucket-name "story-planner"
                :key file-name)
  file-name)

