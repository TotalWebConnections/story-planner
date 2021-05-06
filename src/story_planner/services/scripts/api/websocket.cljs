(ns story-planner.services.scripts.api.websocket
  (:require [wscljs.client :as ws]
            [wscljs.format :as fmt]
            [story-planner.config :refer [ws-api]]
            [story-planner.services.scripts.api.localstorage :as localstorage]
            [story-planner.services.scripts.navigation :refer [navigate]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.services.state.global :refer [get-current-user-token get-from-state]]))

(declare send-message)

(defmulti handle-websocket-message (fn [data] (:type data)))

(defmethod handle-websocket-message "new-project"
  [data]
  (handle-state-change {:type "new-project" :value (:data data)}))
(defmethod handle-websocket-message "delete-project"
  [data]
  (handle-state-change {:type "delete-project" :value (:data data)}))
(defmethod handle-websocket-message "projects"
  [data]
  (handle-state-change {:type "get-projects" :value (:data data)}))
(defmethod handle-websocket-message "get-authorized-users"
  [data]
  (handle-state-change {:type "get-authorized-users" :value (:data data)}))
(defmethod handle-websocket-message "project-first"
  [data]
  (handle-state-change {:type "get-project" :value (:data data)}))
(defmethod handle-websocket-message "get-images"
  [data]
  (handle-state-change {:type "get-images" :value (:data data)}))
(defmethod handle-websocket-message "project"
  [data]
  (handle-state-change {:type "get-project" :value (:data data)}))
(defmethod handle-websocket-message "BAD-TOKEN-REQUEST"
  [data]
  (js/alert "Your session has expired, please login again to continue.")
  (localstorage/delete-localstorage-val)
  (navigate "login"))

(defmethod handle-websocket-message :default [data]
  (print "Default Called"))

(defn handle-onOpen []
  (print "Connection Opened")
  (send-message {:type "start-connection"}))

(defn handle-onClose []
  (print "Connection Closed"))

(defn handle-onMessage [e]
  (handle-websocket-message (js->clj (.parse js/JSON (.-data e)) :keywordize-keys true)))

(def handlers {:on-message (fn [e] (handle-onMessage e))
               :on-open    #(handle-onOpen)
               :on-close   #(handle-onClose)})



(defn init-websocket-connection []
  (if (not (exists? socket)) ; TODO test should prevent multiple socket connections
    (def socket (ws/create ws-api handlers))))


(defn send-message [value]
  "here we wrap all of our requests in our token - all ws should be auth - we also use ID to check user faster"
  (ws/send socket (conj value {:token (get-current-user-token) :_id (:_id (get-from-state "user"))}) fmt/json))

(defn close-connection []
  (ws/close socket)
  (js/alert "Connection Closed"))

