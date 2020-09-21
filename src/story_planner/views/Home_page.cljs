(ns story-planner.views.Home_page
  (:require [story-planner.services.scripts.navigation :refer [navigate]]))


(defn Home-page []
  (fn []
    [:div.Home
     [:div.Home__header
      [:p {:on-click #((navigate "signup"))} "Sign up"]
      [:p {:on-click #((navigate "login"))} "Login"]]

     [:h1 "Welcome to reagent-template"]]))
