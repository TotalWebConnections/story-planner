(ns story-planner.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.core :as r]
              [reitit.frontend :as rf]
              [reitit.frontend.easy :as rfe]
              [reitit.coercion :as rc]
              [reitit.coercion.spec :as rss]
              [clerk.core :as clerk]
              [accountant.core :as accountant]
              [story-planner.services.scripts.api.websocket :refer [init-websocket-connection]]
              [story-planner.services.scripts.api.api :as api]
              [story-planner.services.state.global :refer [app-state]]
              [story-planner.views.Home_page :refer [Home-page]]
              [story-planner.views.App_page :refer [App-page]]
              [story-planner.views.Project_page :refer [Project-page]]
              [story-planner.services.state.global :refer [app-state]]
              [story-planner.services.state.dispatcher :refer [handle-state-change]]))

(enable-console-print!)

(defonce match (r/atom nil)) ; this is our current page - we define it here outside our normal data flow

;Base for our authenticated pages
(defn Auth-base [app-state]
  (init-websocket-connection)
  ; TODO this works for testing but needs to be moved for prod
  (js/setTimeout #(api/get-projects) 2000)
  [:div.Main
    (if @match
      (let [view (:view (:data @match))]
        [view app-state]))])

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
    {:name ::frontpage
     :view App-page}]

  ["/projects"
    {:name ::projects
     :view Project-page}]

   ["/home"
    {:name ::home
     :view Home-page}]])

(defn init! []
  (rfe/start!
    (rf/router routes {:data {:coercion rss/coercion}})
    (fn [m] (reset! match m))
    ;; set to false to enable HistoryAPI
    {:use-fragment true})
  (r/render [Auth-base app-state] (.getElementById js/document "app")))

(init!)







(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)


