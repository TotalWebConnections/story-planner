(ns story-planner.server.services.database
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.operators :refer :all]
           [mount.core :refer [defstate]]
           [config.core :refer [env]]))

(defstate db*
  :start (-> env :database-url mg/connect-via-uri)
  :stop (-> db* :conn mg/disconnect))

(defstate db
  :start (:db db*))

(defn test-insert []
  (mc/insert-and-return db "Stuff" {:test "hello"}))

; (let [conn (mg/connect {:host "db.megacorp.internal" :port 7878})])

(defn get-document-by-type [])
(defn get-all-documents-for-user [])

; Need to add our CRUD stuff here

; TODO probably best to keep this all under the project as one big entity
(defn create-folder [folderData]
  "Inserts a new folder"
  (mc/insert-and-return db "folders" folderData))