(ns story-planner.core
    (:require-macros [cljs.core.async.macros :refer [go]])
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.core :as r]
              [reitit.frontend :as rf]
              [reitit.frontend.easy :as rfe]
              [reitit.frontend.controllers :as rfc]
              [reitit.coercion :as rc]
              [reitit.coercion.spec :as rss]
              [clerk.core :as clerk]
              [accountant.core :as accountant]
              [cljs.test :refer-macros [deftest is testing run-tests]]
              [story-planner.services.scripts.api.websocket :refer [init-websocket-connection]]
              [story-planner.services.scripts.api.api :as api]
              [story-planner.services.state.global :refer [app-state]]
              [story-planner.views.Home_page :refer [Home-page]]
              [story-planner.views.App_page :refer [App-page]]
              [story-planner.views.Project_page :refer [Project-page]]
              [story-planner.views.Login_page :refer [Login-page]]
              [story-planner.views.Signup_page :refer [Signup-page]]
              [story-planner.views.Profile_page :refer [Profile-page]]
              [story-planner.services.state.global :refer [app-state]]
              [story-planner.services.state.dispatcher :refer [handle-state-change]]
              [story-planner.services.scripts.api.permissions :refer [check-token login-failed]]))

(enable-console-print!)

(defonce match (r/atom nil)) ; this is our current page - we define it here outside our normal data flow
; TODO this works for testing but needs to be moved for prod
(js/setTimeout #(api/get-projects (:token (:user @app-state))) 1000)
(js/setTimeout #(api/get-authorized-users) 1000)


(defn generate-base-html []
  [:div.Main
    (if @match
      (let [view (:view (:data @match))]
        [view app-state]))])

(defn handle-permissions-flow []
  (let [chan (check-token (:token (:user @app-state)))]
    ; TODO we should cache a valid token in the state here so we can limit api requests and it shouldn't change that often
    (go (let [response (<! chan)
              response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
          (if (:data response-body)
            (generate-base-html)
            (login-failed))))))

;Base for our authenticated pages
(defn Auth-base [app-state]
  (init-websocket-connection)
  (let [route-data (:data @match)]
    (if (not (:public? route-data))
      (handle-permissions-flow))
    (generate-base-html)))

;Base for our pulbic facing pages
(defn Base-page [app-state] ; Our base to hold the shell of our application - probably move this once it gets bigger
  [:div
   (if (not= "app" (:navType @app-state))
     [:ul
      [:li [:a {:href (rfe/href ::frontpage)} "Frontpage"]]
      [:li [:a {:href (rfe/href ::home)} "home"]]])

   (if @match
     (let [view (:view (:data @match))]
       [view app-state]))])

(def routes
  [["/"
    {:name ::home
     :view Home-page
     :public? true}]


   ["/login"
     {:name ::login
      :view Login-page
      :public? true}]

   ["/signup"
     {:name ::signup
      :view Signup-page
      :public? true}]

   ["/app"
    {:name ::frontpage
     :view App-page
     :public? false}]

   ["/projects"
     {:name ::projects
      :view Project-page
      :public? false}]

   ["/profile"
     {:name ::profile
      :view Profile-page
      :public? false}]])

(defn move-localstorage-to-state [app-state]
  "sets our state to have localstorage vals to persist login"
  (let [localstorage (js->clj (js/JSON.parse (.getItem js/localStorage "story-planner-token")) :keywordize-keys true)]
    (if (and (not (:user @app-state)) localstorage)
      (handle-state-change {:type "set-user" :value localstorage}))))

(defn init! []
  (move-localstorage-to-state app-state)
  (rfe/start!
    (rf/router routes {:data {:coercion rss/coercion}})
    (fn [new-match]
      (swap! match
        (fn [old-match]
          (when new-match
            (assoc new-match :controllers (rfc/apply-controllers (:controllers old-match) new-match))))))

    ;; set to false to enable HistoryAPI
    {:use-fragment true})
  (r/render [Auth-base app-state] (.getElementById js/document "app")))

(init!)







(defn on-js-reload [])
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)



