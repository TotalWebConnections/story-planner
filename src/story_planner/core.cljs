(ns story-planner.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.core :as r]
              [reitit.frontend :as rf]
              [reitit.frontend.easy :as rfe]
              [reitit.coercion :as rc]
              [reitit.coercion.spec :as rss]
              [clerk.core :as clerk]
              [accountant.core :as accountant]
              [story-planner.views.page :as page]
              [story-planner.services.state.global :refer [app-state]]
              [story-planner.services.state.dispatcher :refer [handle-state-change]]
              [story-planner.components.Canvas :as Canvas]
              [story-planner.components.Sidebar :refer [Sidebar]]))

(enable-console-print!)

(defonce match (r/atom nil)) ; this is our current page - we define it here outside our normal data flow

(defn current-page []
  [:div
   [:ul
    [:li [:a {:href (rfe/href ::frontpage)} "Frontpage"]]
    [:li [:a {:href (rfe/href ::home)} "home"]]]
   (if @match
     (let [view (:view (:data @match))]
       [view @match]))])

(defn core []
  [:div.Main
    [Canvas/render]
    [Sidebar]
    [:a {:href (rfe/href ::home)} "About reagent-template"]
    [:p {:on-click #(handle-state-change {:type "update-state-text" :value "Test Text Here"})} "Click to update state text"]
    [page/render (:example-page (:active-page @app-state))]])

(defn home-page []
  (fn []
    [:div.main
     [:h1 "Welcome to reagent-template"]]))

(defn mount-root []
  (reagent/render-component [core]
                          (. js/document (getElementById "app"))))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def routes
  [["/"
    {:name ::frontpage
     :view core}]

   ["/home"
    {:name ::home
     :view home-page}]])

(defn init! []
  (rfe/start!
    (rf/router routes {:data {:coercion rss/coercion}})
    (fn [m] (reset! match m))
    ;; set to false to enable HistoryAPI
    {:use-fragment true})
  (r/render [current-page] (.getElementById js/document "app")))

(init!)


