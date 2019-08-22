(ns story-planner.services.scripts.api.websocket
  (:require [wscljs.client :as ws]
            [wscljs.format :as fmt]))

(defn handle-onOpen []
  (print "Connection Opened"))

(defn handle-onClose []
  (print "Connection Closes"))

(defn handle-onMessage [e]
  ; TODO multi method implementation here
  (js/console.log e))

(def handlers {:on-message (fn [e] (handle-onMessage e))
               :on-open    #(handle-onOpen)
               :on-close   #(handle-onClose)})



(defn init-websocket-connection []
  (def socket (ws/create "ws://localhost:8080" handlers)))


(defn send-message [value]
  (ws/send socket value fmt/json))

(defn close-connection []
  (ws/close socket)
  (js/alert "Connection Closed"))

