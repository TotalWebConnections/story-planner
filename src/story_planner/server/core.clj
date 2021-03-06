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
    [story-planner.server.services.amazon :refer [handle-image-upload handle-delete-image]]
    [story-planner.server.services.database.media :refer [add-media-folder]]
    [story-planner.server.services.database.authorized :as auth]
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
(defn send-message-to-all [user projectId msg]
  "Sends a message to all connected ws connections"
    (if (or (= "project" (:type msg)) (= "all" (:msg-type msg))) ; TODO this needs to be removed after we'ere done - just for testing
      (let [authorizedUsers (auth/get-authorized-users-from-project projectId)]
        (doseq [ch @channel-store]
          (if (or
                (= (:id ch) (:_id user))
                (some #(= (str (:id ch))  %) authorizedUsers)
                (= (str (:id ch)) (:userId (first (:data msg)))))
            (async/send! (:channel ch) (generate-string msg)))))
      (send-message-to-user msg (:_id user))))

; (:id (ws/session h) get user ID of message
; need to be able to pool these and send it out to all with ID
(def websocket-callbacks
  "WebSocket callback functions"
  {:on-open   (fn [channel]
               ; (swap! channel-store conj channel) ; store channels for later
               (async/send! channel (generate-string {:type "onReady" :data "Succesful connection"})))
   :on-close   (fn [channel {:keys [code reason]}]
                (swap! channel-store (partial filter (fn [chan] (if (= (:channel chan) channel) false true)))) ; removes channels on disconnect
                (println "close code:" code "reason:" reason))
   :on-message (fn [ch m]
                (if-not (= "ping" (:type (parse-string m true))) ; ping command stops inactive timeouts
                  (let [user (DB-users/get-user-by-token  (:_id (parse-string m true)) (:token (parse-string m true)))]
                    (if user
                      (if (= (:type (parse-string m true)) "start-connection")
                        (swap! channel-store conj {:channel ch :id (:_id user)})
                        (let [msg (parse-string m true)]
                          (send-message-to-all user (:projectId msg) (socketHandlers/handle-websocket-message (conj msg {:channel ch :user user})))))
                      (async/send! ch (generate-string {:type "BAD-TOKEN-REQUEST" :data "Bad Token"}))))
                  (async/send! ch (generate-string {:type "ping" :data "ping"}))))})
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
  (POST "/create-media-folder" request
    (let [params (walk/keywordize-keys (:form-params request))]
      (response (generate-string (DB-users/add-user-media-folder (:_id params) (:token params) (:folder params))))))
  (POST "/delete-image" request
    (let [params (walk/keywordize-keys (:form-params request))]
      (response
        (generate-string
          (handle-delete-image
            (DB-users/remove-image (:_id params) (:token params) (:url params)))))))
  (POST "/user" request
    (response (generate-string (handle-save-user  (walk/keywordize-keys (:form-params request))))))
  (POST "/login" request
    (response (generate-string (handle-login-user (walk/keywordize-keys (:form-params request))))))
  (POST "/check-token" request
    (try
      (let [user (walk/keywordize-keys (:form-params request))]
        (response (generate-string (check-user-token (:_id user) (:token user)))))
      (catch Exception e {:status 403 :body (str "caught exception: " (.getMessage e))})))
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
    (merge {"host" "0.0.0.0", "port" (Integer. (or (System/getenv "PORT") 8080))}
     args)))
