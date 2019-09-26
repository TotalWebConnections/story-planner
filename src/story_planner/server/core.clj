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
    [story-planner.server.services.database :as DB])
  (:gen-class))

(mount.core/start) ; Starts our DB

; GENERAL FLOW FOR CHANNELS
; on each connection we setup our channel and send a message to the client with the {:connected true} key
; client receives the message connected and sends a reply with {:boardID, uniqueId: token:}
; we take all that and push the channel to a new atom that is similar to {:boardId: channel}
; this way we can group all the connected users together, and on each request push changes to the
; given channel.


; TODO items
; Disconnect function async/close - DONE
; Auth roles - probably just going to use mongo for this
; Image uploads - probably a pita
; We do need ids for users - that way we can spring up notificiations - "An entity was added!" shit like that
; Setup multi method to dispatch actions based on the type of the request coming in
;   -:new-connection
;   -:update-map
;   -:update-entities

; TODO move this out of hte core here - separation
; TODO we also need to look at the performance of just sending back
; The whole project on each request - front end is probably smart enough to
; take the override and update teh DOM the accordingly which is what I'd expect
; teh react/reagent wrapper to do, but we need to test this for a larger project with
; multiple users working at the same time - performance might suffer and then we may need
; to break things apart a bit more to facilitate udpates of individual pieces of state
(defn construct-all-project-return [query]
  (map (fn [project]
    {:_id (:_id project) :name (:name project)} ) query))


(defmulti handle-websocket-message (fn [data] (:type data)))
  (defmethod handle-websocket-message "create-project"
    [data]
    (async/send! (:channel data)
                 (generate-string
                  (dissoc (DB/create-project {:name (:value data) :userId "123"}) :_id))))
  (defmethod handle-websocket-message "create-folder"
    [data]
    (async/send! (:channel data)
                 (generate-string
                   {:type "project" :data (DB/create-folder {:name (:value data) :type (:folder data) :id (:projectId data)})})))
  (defmethod handle-websocket-message "create-entity"
    [data]
    (async/send! (:channel data) ; TODO this can be moved to a heler since we'll probably re-use it a on most API calls for the time being
      (generate-string
        {:type "project" :data (DB/create-entity (dissoc data :channel))})))
  (defmethod handle-websocket-message "create-board"
    [data]
    (async/send! (:channel data) ; TODO this can be moved to a heler since we'll probably re-use it a on most API calls for the time being
      (generate-string
        {:type "project" :data (DB/create-board (dissoc data :channel))})))
  (defmethod handle-websocket-message "create-storypoint"
    [data]
    (async/send! (:channel data) ; TODO this can be moved to a heler since we'll probably re-use it a on most API calls for the time being
      (generate-string
        {:type "project" :data (DB/create-storypoint (dissoc data :channel))})))
  (defmethod handle-websocket-message "get-projects"
    [data] ; Returns the name and ID of all projects
    (async/send! (:channel data)
      (generate-string
        {:type "projects" :data (construct-all-project-return (DB/get-projects (:value data)))})))
  (defmethod handle-websocket-message "get-project"
    [data] ; Returns the name and ID of all projects
    (async/send! (:channel data)
      (generate-string
        {:type "project" :data (DB/get-project (:value data))})))
  (defmethod handle-websocket-message "update-storypoint-position"
    [data] ; Returns the name and ID of all projects
    (async/send! (:channel data)
      (generate-string
        {:type "project" :data (DB/update-storypoint-position {:storypointId (:storypointId data) :position (:position data) :id (:projectId data)})})))
  (defmethod handle-websocket-message :default [data]
    (async/send! (:channel data) (generate-string "No method signiture found"))) ; String for consistency sake


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
    (handle-websocket-message (conj (parse-string m true) {:channel ch})))})


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