(ns story-planner.server.core
  (:require
    [immutant.web             :as web]
    [immutant.web.async       :as async]
    [immutant.web.middleware  :as web-middleware]
    [compojure.route          :as route]
    [environ.core             :refer (env)]
    [compojure.core           :refer (ANY GET defroutes)]
    [ring.util.response       :refer (response redirect content-type)]
    [cheshire.core            :refer :all]
    [mount.core :refer :all]
    [story-planner.server.services.database :as DB]
    [story-planner.server.services.socket :as socketHandlers])
  (:gen-class))

(mount.core/start) ; Starts our DB

(def channel-store (atom []))

(defn send-message-to-all []
  "Sends a message to all connected ws connections"
    (doseq [ch @channel-store]
      (async/send! ch "Message Received")))

; (:id (ws/session h) get user ID of message
; need to be able to pool these and send it out to all with ID
(def websocket-callbacks
  "WebSocket callback functions"
  {:on-open   (fn [channel]
               (swap! channel-store conj channel) ; store channels for later
               (async/send! channel (generate-string {:type "onReady" :data "Ready to reverse your messages!"})))
   :on-close   (fn [channel {:keys [code reason]}]
    ; (swap! channel-store filter (fn [chan] (if (= chan channel) true false)) channel-store) close enough
                (println "close code:" code "reason:" reason))
   :on-message (fn [ch m]
                (socketHandlers/handle-websocket-message (conj (parse-string m true) {:channel ch})))})


(defroutes routes
  (GET "/" {c :context} (redirect (str c "/index.html")))
  (route/resources "/"))

(defn -main [& {:as args}]
  (web/run
    (-> routes
      ; (web-middleware/wrap-session {:timeout 20})
      ;; wrap the handler with websocket support
      ;; websocket requests will go to the callbacks, ring requests to the handler
      (web-middleware/wrap-websocket websocket-callbacks))
    (merge {"host" (env :demo-web-host), "port" 8080}
     args)))