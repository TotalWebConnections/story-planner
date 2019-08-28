(ns story-planner.services.scripts.api.websocket
  (:require [wscljs.client :as ws]
            [wscljs.format :as fmt]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))


(defmulti handle-websocket-message (fn [data] (:type data)))
  (defmethod handle-websocket-message "projects"
    [data]
    (handle-state-change {:type "get-projects" :value (:data data)}))
  (defmethod handle-websocket-message :default [data]
    (print "Default Called"))

(defn handle-onOpen []
  (print "Connection Opened"))

(defn handle-onClose []
  (print "Connection Closes"))

(defn handle-onMessage [e]
  (handle-websocket-message (js->clj (.parse js/JSON (.-data e)) :keywordize-keys true)))

(def handlers {:on-message (fn [e] (handle-onMessage e))
               :on-open    #(handle-onOpen)
               :on-close   #(handle-onClose)})



(defn init-websocket-connection []
  (if (not socket)
    (def socket (ws/create "ws://localhost:8080" handlers))))


(defn send-message [value]
  (ws/send socket value fmt/json))

(defn close-connection []
  (ws/close socket)
  (js/alert "Connection Closed"))

