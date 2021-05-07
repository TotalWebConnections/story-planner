(ns story-planner.services.scripts.api.localstorage
  (:require [story-planner.services.state.dispatcher :refer [handle-state-change]]))


(defn set-localstorage-vals [value]
  "we do this so that a user can stay logged in between views
   a bit of duplication by having it in state as well, but this should really only be used on initial load
   or if the user updates something on their acct like subs or email"
  (.setItem js/localStorage "story-planner-token" (js/JSON.stringify (clj->js value))))

(defn delete-localstorage-val []
  "We use this for logout stuff"
  (.removeItem js/localStorage "story-planner-token")
  (handle-state-change {:type "set-user" :value nil}))

(defn update-localstorage-by-key [key value]
  (let [current-user (js->clj (js/JSON.parse (.getItem js/localStorage "story-planner-token")) :keywordize-keys true)
        updated-user (conj current-user {(keyword key) value})]
    (set-localstorage-vals updated-user)
    (handle-state-change {:type "set-user" :value updated-user})))

(defn update-local-storage [user]
  (set-localstorage-vals user)
  (handle-state-change {:type "set-user" :value user}))
