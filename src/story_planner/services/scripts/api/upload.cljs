(ns story-planner.services.scripts.api.upload
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [story-planner.config :refer [api]]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))


(defn upload-image [image-field folder]
  (let [id (:_id (get-from-state "user"))]
    (let  [my-file (first (array-seq (.-files (.getElementById js/document image-field))))]

      (go (let [response (<! (http/post (str api "/upload-img")
                                     {:with-credentials? false
                                      :multipart-params [["myFile" my-file] ["token" (:token (get-from-state "user"))] ["_id" id] ["folder" folder]]}))
                parsed-response (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
            (if (= parsed-response  "Must Be An Image")
              (js/alert "File Must Be an Image")
              (if (= parsed-response  "Image Too Large")
                (js/alert "Image Must Be Under 10MB")
                (handle-state-change  {:type "add-image" :value parsed-response}))))))))


(defn create-media-folder [folder-name]
  (let [id (:_id (get-from-state "user"))]
    (go (let [response (<! (http/post (str api "/create-media-folder")
                                   {:with-credentials? false
                                    :form-params {:folder @folder-name :_id id :token (:token (get-from-state "user"))}}))
              response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
          (if (= (:type response-body) "error")
            (print "error")
            (handle-state-change {:type "add-media-folder" :value response-body}))))))

(defn delete-image [url]
  (let [id (:_id (get-from-state "user"))]
    (go (let [response (<! (http/post (str api "/delete-image")
                                   {:with-credentials? false
                                    :form-params {:url url :_id id :token (:token (get-from-state "user"))}}))
              response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
          (handle-state-change  {:type "remove-image" :value response-body})))))
