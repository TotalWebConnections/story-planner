(ns story-planner.services.scripts.api.upload
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))


(defn upload-image [image-field folder]
  (let  [my-file (first (array-seq (.-files (.getElementById js/document image-field))))]

    (go (let [response (<! (http/post "http://localhost:8080/upload-img"
                                   {:with-credentials? false
                                    :multipart-params [["myFile" my-file] ["token" (:token (get-from-state "user"))] ["folder" folder]]}))]

          (handle-state-change  {:type "add-image" :value (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)})))))
  

(defn create-media-folder [folder-name]
  (go (let [response (<! (http/post "http://localhost:8080/create-media-folder"
                                 {:with-credentials? false
                                  :form-params {:folder @folder-name :token (:token (get-from-state "user"))}}))
            response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
        (if (= (:type response-body) "error")
          (print "error")
          (print response-body)))))

(defn delete-image [url]
  (go (let [response (<! (http/post "http://localhost:8080/delete-image"
                                 {:with-credentials? false
                                  :form-params {:url url :token (:token (get-from-state "user"))}}))
            response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
        (handle-state-change  {:type "remove-image" :value response-body}))))
