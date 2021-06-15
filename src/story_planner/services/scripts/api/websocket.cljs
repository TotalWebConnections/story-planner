(ns story-planner.services.scripts.api.websocket
  (:require [wscljs.client :as ws]
            [wscljs.format :as fmt]
            [story-planner.config :refer [ws-api]]
            [story-planner.services.scripts.api.localstorage :as localstorage]
            [reitit.frontend.easy :as rfe]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.services.state.global :refer [get-current-user-token get-from-state]]
            [story-planner.services.scripts.debounce :refer [debounce]]))

(declare send-message)
(def socket (atom nil))

(def intervalRef (atom nil))

(defn handle-expired-token
  "callback for if the server token doesn't match the UI one - if a user logs in twice this might happen"
  []
  (js/alert "Your session has expired, please login again to continue.")
  (localstorage/delete-localstorage-val)
  (rfe/push-state :login))

(def handle-expired-token-debounced!
  (debounce handle-expired-token 500))

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
  (handle-expired-token-debounced!))

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

(defn do-ping []
  (ws/send @socket {:type "ping"} fmt/json))

(defn setup-ping []
  "required or heroku server will timeout after 55 seconds of inactvity"
  (reset! intervalRef (js/setInterval #(do-ping) 10000)))

(defn init-websocket-connection []
  (if (not @socket); TODO test should prevent multiple socket connections
    (do
      (setup-ping)
      (reset! socket (ws/create ws-api handlers)))))


(defn send-message [value]
  "here we wrap all of our requests in our token - all ws should be auth - we also use ID to check user faster"
  (ws/send @socket (conj value {:token (get-current-user-token) :_id (:_id (get-from-state "user"))}) fmt/json))

(defn close-connection []
  (if @socket ; if a socket exists we close it and cleaup
    (do
      (ws/close @socket)
      (js/clearInterval @intervalRef)
      (reset! socket nil))))
  ; (js/alert "Connection Closed"))

