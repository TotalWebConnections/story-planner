(ns story-planner.server.services.database.folders
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.response-handler :as response-handler]
           [story-planner.server.services.database.projects :refer [get-project]])
  (:import org.bson.types.ObjectId))


(defn create-folder [folderData userId]
  "Inserts a new folder"
  (let [projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id folderData))}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$push {:folders (dissoc folderData :id)}} {:upsert true}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "new-folder" "all" folderData)
      (response-handler/send-auth-error))))