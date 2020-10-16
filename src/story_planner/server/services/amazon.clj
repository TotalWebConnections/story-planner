(ns story-planner.server.services.amazon
  (:require [config.core :refer [env]]
            [story-planner.server.services.database :as DB])
  (:use [amazonica.aws.s3]))



(defn handle-image-upload [file]
  (let [user (DB/get-user-by-token (get file "token"))]
    (if user
      (put-object (:s3creds env)
            :bucket-name "story-planner"
            :key (str (str (:_id user)) "/" (:filename (get file "myFile")))
            :file (:tempfile (get file "myFile")))
      "User Auth Failed")))


(defn handle-load-images [id])
