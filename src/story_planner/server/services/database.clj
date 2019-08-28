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
; TODO move these
;CREATE METHODS
(defn create-folder [folderData]
  "Inserts a new folder"
  (mc/insert-and-return db "folders" folderData))

(defn create-project [projectData]
  "insers a new project for current user"
  (mc/insert-and-return db "projects" projectData))




; READ METHODS
; TODO remove let - can simplify a bit
(defn get-projects [userId]
  (let [projects (mc/find-maps db "projects" {:userId userId})]
    (map ; Turn characters into a modified list
      #(update % :_id str) ; By updating each map :id by casting to a string
      projects)))


