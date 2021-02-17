(ns story-planner.services.scripts.api.permissions
  (:require [story-planner.services.scripts.navigation :refer [navigate]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [cljs-http.client :as http]
            [story-planner.config :refer [api]]))

(defn login-failed []
  (handle-state-change {:type "set-login-error" :value "You need to be logged in to view that page"})
  (navigate "login"))

(defn check-token [token]
  (http/post (str api "/check-token")
             {:with-credentials? false
              :form-params {:token token}}))
