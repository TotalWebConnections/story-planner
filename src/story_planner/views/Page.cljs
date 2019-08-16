(ns story-planner.views.page
  (:require [story-planner.services.state.dispatcher :refer [handle-state-change]]))



(defn render [active]
  [:div.Page {:class active}
    [:p {:on-click #(handle-state-change {:type "update-active-view" :value ""})}"Go Back"]
    [:h2 "I'm a page that takes my state"]])