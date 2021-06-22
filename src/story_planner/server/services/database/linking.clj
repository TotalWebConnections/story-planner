(ns story-planner.server.services.database.linking
  (:require [monger.core :as mg]
           [monger.collection :as mc]
           [monger.conversion :as mgcon]
           [monger.operators :refer :all]
           [story-planner.server.services.database :refer [db]]
           [story-planner.server.services.response-handler :as response-handler])
  (:import org.bson.types.ObjectId))


(defn add-link-to-storypoint [storyData userId]
  (let [linkId (str (ObjectId.))
        projectUpdate (.getN (mc/update db "projects" {$and [{:_id (ObjectId. (:id storyData))}
                                                             {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                             {$or [{:userId userId}
                                                                   {:authorizedUsers {$in [(str userId)]}}]}]}
                                                      {$push {"storypoints.$.links" {:id (:value storyData) :linkId linkId}}}))]
    (if (> projectUpdate 0)
      (response-handler/wrap-ws-response "add-link-to-storypoint" "all" {:id (:value storyData) :linkId linkId :storypointId (:storypointId storyData)})
      (response-handler/send-auth-error))))

(defn update-link-label [storyData userId]
  "This one is a bit different since the command results is different - we have to use that as the update filters we used
    aren't supported by the core monger - have to access it with a manual mongo command"
  (let [projectUpdate (.ok (mg/command db (sorted-map :update "projects"
                                              :updates [{:q {$and [{:_id (ObjectId. (:id storyData))}
                                                                   {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                                   {$or [{:userId userId}
                                                                         {:authorizedUsers {$in [(str userId)]}}]}]}
                                                         :u {"$set" {"storypoints.$.links.$[link].label" (:label storyData)}} :arrayFilters [ {"link.linkId" (:linkId storyData)}]}])))]
    (if projectUpdate
      (response-handler/wrap-ws-response "update-link-label" "all" (select-keys storyData [:storypointId :linkId :label]))
      (response-handler/send-auth-error))))

(defn delete-link [storyData userId]
  (let [projectUpdate (mg/command db (sorted-map :update "projects"
                                              :updates [{:q {$and [{:_id (ObjectId. (:id storyData))}
                                                                   {:storypoints {$elemMatch {:id (:storypointId storyData)}}}
                                                                   {$or [{:userId userId}
                                                                         {:authorizedUsers {$in [(str userId)]}}]}]}
                                                         :u {$pull {"storypoints.$.links" {:linkId (:linkId storyData)}}}}]))]
    (if projectUpdate
      (response-handler/wrap-ws-response "delete-link" "all" (select-keys storyData [:storypointId :linkId]))
      (response-handler/send-auth-error))))
