(ns story-planner.server.services.amazon
  (:require [config.core :refer [env]]
            [story-planner.server.services.database.users :as DB-users]
            [story-planner.server.services.database.media :as media])
  (:use [amazonica.aws.s3]))



(defn handle-image-upload [file]
  (let [user (DB-users/get-user-by-token (get file "token"))
        folder (get file "folder")]
    (if user
      (do
        (put-object (:s3creds env)
              :bucket-name "story-planner"
              :key (str (str (:_id user)) "/" (:filename (get file "myFile")))
              :file (:tempfile (get file "myFile")))
        (media/add-media (:_id user) (str (str (:_id user)) "/" (:filename (get file "myFile"))) folder))
      "User Auth Failed")))


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

