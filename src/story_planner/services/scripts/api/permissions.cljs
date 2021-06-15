(ns story-planner.services.scripts.api.permissions
  (:require [clojure.core.async :refer [chan]]
            [reitit.frontend.easy :as rfe]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.services.scripts.api.websocket :as ws]
            [cljs-http.client :as http]
            [story-planner.config :refer [api]]
            [story-planner.services.state.global :refer [get-from-state]]))

(defn login-failed []
  (handle-state-change {:type "set-login-error" :value "You need to be logged in to view that page"})
  (ws/close-connection) 
  (rfe/push-state :login))

(defn check-token [token]
  (let [id (:_id (get-from-state "user"))]
    (if id
      (http/post (str api "/check-token")
                 {:with-credentials? false
                  :form-params {:_id  id :token token}})
      (login-failed))))
