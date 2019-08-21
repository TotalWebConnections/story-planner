(ns story-planner.services.scripts.api.websocket
  (:require [wscljs.client :as ws]
            [wscljs.format :as fmt]))

(defn handle-onOpen []
  (js/alert "Connection Opened"))
(defn handle-onClose [])
(defn handle-onMessage [e]
  (js/console.log e))

(def handlers {:on-message (fn [e] (handle-onMessage e))
               :on-open    #(handle-onOpen)
               :on-close   #(handle-onClose)})



(defn init-websocket-connection []
  (def socket (ws/create "ws://localhost:8080" handlers)))


(defn send-message []
  (ws/send socket "value" fmt/json))

(defn close-connection []
  (ws/close socket)
  (js/alert "Connection Closed"))

