(ns story-planner.services.scripts.api.upload
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))


(defn upload-image [image-field]
  (let  [my-file (first (array-seq (.-files (.getElementById js/document image-field))))]

    (go (let [response (<! (http/post "http://localhost:8080/upload-img"
                                   {:with-credentials? false
                                    :multipart-params [["myFile" my-file] ["token" (:token (get-from-state "user"))]]}))]


          (handle-state-change  {:type "add-image" :value (js/JSON.parse (:body response))})))))
