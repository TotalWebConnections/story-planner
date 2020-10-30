(ns story-planner.server.core
  (:require
    [immutant.web             :as web]
    [immutant.web.async       :as async]
    [immutant.web.middleware  :as web-middleware]
    [compojure.route          :as route]
    [environ.core             :refer (env)]
    [compojure.core           :refer (ANY GET POST defroutes)]
    [ring.util.response       :refer (response redirect content-type)]
    [ring.middleware.params   :refer [wrap-params]]
    [ring.middleware.multipart-params     :refer [wrap-multipart-params]]
    [cheshire.core            :refer :all]
    [mount.core :refer :all]
    [clojure.walk :as walk]
    [story-planner.server.services.database.users :as DB-users]
    [story-planner.server.services.socket :as socketHandlers]
    [story-planner.server.services.amazon :refer [handle-image-upload]]
    [story-planner.server.services.user :refer [handle-save-user handle-login-user check-user-token subscribe-user unsubscribe-user signup-auth-user]])
  (:gen-class))

(mount.core/start) ; Starts our DB

(def channel-store (atom []))

(defn send-message-to-user [msg id]
  "used to send a messsage to a specific user - e.x query projects"
  (doseq [ch @channel-store]
    (if (= (:id ch) id)
      (async/send! (:channel ch) (generate-string msg)))))
; (some #(= (:_id user) %) (:authorizedUsers project))
(defn send-message-to-all [user msg]
  "Sends a message to all connected ws connections"
    (doseq [ch @channel-store]
      ; (println (:authorizedUsers (first (:data msg))))
      (if (= "project" (:type msg))
          (if (or
                (= (:id ch) (:_id user))
                (some #(= (str (:id ch))  %) (:authorizedUsers (first (:data msg))))
                (= (str (:id ch)) (:userId (first (:data msg)))))
            (async/send! (:channel ch) (generate-string msg)))
        (send-message-to-user msg (:_id user)))))

; (:id (ws/session h) get user ID of message
; need to be able to pool these and send it out to all with ID
(def websocket-callbacks
  "WebSocket callback functions"
  {:on-open   (fn [channel]
               ; (swap! channel-store conj channel) ; store channels for later
               (async/send! channel (generate-string {:type "onReady" :data "Succesful connection"})))
   :on-close   (fn [channel {:keys [code reason]}]
    ; (swap! channel-store filter (fn [chan] (if (= chan channel) true false)) channel-store) close enough
                (println "close code:" code "reason:" reason))
   :on-message (fn [ch m]
                ; (println (parse-string m true))
                (let [user (DB-users/get-user-by-token (:token (parse-string m true)))]
                  (if user
                    (if (= (:type (parse-string m true)) "start-connection")
                      (swap! channel-store conj {:channel ch :id (:_id user)}) ; store channels for later
                      (send-message-to-all user (socketHandlers/handle-websocket-message (conj (parse-string m true) {:channel ch :user user}))))
                    (async/send! ch (generate-string {:type "BAD-TOKEN-REQUEST" :data "Bad Token"})))))})
(def cors-headers
  { "Access-Control-Allow-Origin" "*"
    "Access-Control-Allow-Headers" "Content-Type"
    "Access-Control-Allow-Credentials" "false"
    "Access-Control-Allow-Methods" "GET,POST,OPTIONS"})

(defn all-cors
  "Allow requests from all origins"
  [handler]
  (fn [request]
    (let [response (handler request)]
      (update-in response [:headers]
        merge cors-headers))))


(defroutes routes
  (GET "/" {c :context} (redirect (str c "/index.html")))
  (POST "/upload-img" request
    (response (generate-string (handle-image-upload (:multipart-params request)))))
  (POST "/user" request
    (response (generate-string (handle-save-user  (walk/keywordize-keys (:form-params request))))))
  (POST "/login" request
    (response (generate-string (handle-login-user (walk/keywordize-keys (:form-params request))))))
  (POST "/check-token" request
    (try
      (response (generate-string (check-user-token (:token (walk/keywordize-keys (:form-params request))))))
      (catch Exception e (str "caught exception: " (.getMessage e)))))
  (POST "/subscribe" request
    (response (generate-string (subscribe-user (walk/keywordize-keys (:form-params request))))))
  (POST "/unsubscribe" request
    (response (generate-string (unsubscribe-user (walk/keywordize-keys (:form-params request))))))
  (POST "/signup-auth-user" request
    (response (generate-string (signup-auth-user (walk/keywordize-keys (:form-params request))))))
  (route/resources "/"))

(defn -main [& {:as args}]
  (web/run
    (-> routes
      (all-cors)
      (wrap-params)
      (wrap-multipart-params)
      ; (web-middleware/wrap-session {:timeout 20})
      ;; wrap the handler with websocket support
      ;; websocket requests will go to the callbacks, ring requests to the handler
      (web-middleware/wrap-websocket websocket-callbacks))
    (merge {"host" (env :demo-web-host), "port" 8080}
     args)))