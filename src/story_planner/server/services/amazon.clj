(ns story-planner.server.services.amazon
  (:require [config.core :refer [env]])
  (:use [amazonica.aws.s3]))



(defn handle-image-upload [file]
  (put-object (:s3creds env)
        :bucket-name "story-planner"
        :key (str "userID/" (:filename (get file "myFile")))
        :file (:tempfile (get file "myFile"))))
