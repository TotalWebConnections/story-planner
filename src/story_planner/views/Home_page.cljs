(ns story-planner.views.Home_page
  (:require [story-planner.services.scripts.navigation :refer [navigate]]))


(defn Home-page []
  (fn []
    [:div.Home
     [:div.Home__header
      [:h2 "Name"]
      [:ul
       [:p "Pricing"]
       [:p "Features"]
       [:p "Contact"]]
      [:p {:on-click #(navigate "signup")} "Sign up"]
      [:p {:on-click #(navigate "login")} "Login"]
      [:p {:on-click #(navigate "projects")} "projects"]]

     [:div.Home__mainVisual
      [:div.Home__mainVisual__inner
       [:div.Home__mainVisual-left
        [:h1 "Collaborative Design Made Easy"]
        [:p "Visualize and design complex paths collaborativly."]
        [:button "Get Started For Free"]]
       [:div.Home__mainVisual-right
        [:p "Big Image Here"]]]]

     [:div.Home__ribbon
      [:h2 "Used By"]
      [:div.Home__ribbon__wrapper
       [:div
        [:h3 "Game Designers"]]
       [:div
        [:h3 "Software Developers"]]
       [:div
        [:h3 "Writers"]]
       [:div
        [:h3 "RPG Players"]]]]

     [:div.Home__section
      [:div.Home_section--side
       [:h2 "Visualize Complexity"]]
      [:div.Home_section--side
       [:p "img"]]]

     [:div.Home__section.alternate
      [:div.Home_section--side
       [:p "img"]]
      [:div.Home_section--side
       [:h2 "Collaborate Efficiently"]]]

     [:div.Home__section
      [:div.Home_section--side
       [:h2 "Prototype Quickly"]]
      [:div.Home_section--side
       [:p "img"]]]



     [:div.Home__lgSection
      [:div.Home_section--side
       [:h2 "Feature Filled"]]
      [:div.Home_section--side
       [:div
        [:p "Asset Mananger"]
        [:p "Branching Paths"]]
       [:div
        [:p "Visualize at glance"]
        [:p "Conquer Complexity"]]]]


     [:div.Home__ribbon
      [:h2 "Start Your Free Trial"]
      [:button "Start Free - No Credit Card"]]

     [:div.Home__pricing
      [:h2 "One Low Fee - No Suprises"]
      [:p "$9/Month"]
      [:button "Sign Up"]]

     [:div.Home__footer
      [:p "Terms"]
      [:p "Privacy"]
      [:p "Copyright"]]]))




