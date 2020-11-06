(ns story-planner.views.Home_page
  (:require [story-planner.services.scripts.navigation :refer [navigate]]))


(defn Home-page []
  (fn []
    [:div.Home
     [:div.Home__header
      [:div.Home__header__block
       [:h2 "Name"]
       [:ul
        [:li "Pricing"]
        [:li "Features"]
        [:li "Contact"]]]
      [:div.Home__header__block
       [:ul
        [:li {:on-click #(navigate "signup")} "Sign up"]
        [:li {:on-click #(navigate "login")} "Login"]
        [:li {:on-click #(navigate "projects")} "projects"]]]]


     [:div.Home__mainVisual
      [:div.Home__mainVisual__inner
       [:div.Home__mainVisual-left
        [:h1 "Collaborative Design Made Easy"]
        [:p "Visualize and design complex paths collaborativly."]
        [:button "Get Started For Free"]]
       [:div.Home__mainVisual-right
        [:img {:src "/images/placeholder.jpg" :width "100%"}]]]]

     [:div.Home__ribbon
      [:h2 "Used By"]
      [:div.Home__ribbon__wrapper
       [:div
        [:h3 "Game Designers"]
        [:img {:src "/images/placeholder.jpg" :width "150px"}]]
       [:div
        [:h3 "Software Developers"]
        [:img {:src "/images/placeholder.jpg" :width "150px"}]]
       [:div
        [:h3 "Writers"]
        [:img {:src "/images/placeholder.jpg" :width "150px"}]]
       [:div
        [:h3 "RPG Players"]
        [:img {:src "/images/placeholder.jpg" :width "150px"}]]]]

     [:div.Home__section
      [:div.Home_section--side
       [:h2 "Visualize Complexity"]
       [:p "Easily visualize complex flows. Perfect for branching dialogue, modeling user decisions, or telling interactive stories."]]
      [:div.Home_section--side
       [:img {:src "/images/placeholder.jpg" :width "250px"}]]]

     [:div.Home__section.alternate
      [:div.Home_section--side
       [:img {:src "/images/placeholder.jpg" :width "250px"}]]
      [:div.Home_section--side
       [:h2 "Collaborate Efficiently"]
       [:p "Share your project with co-workers and friends and seamlessly work togather on the same story in realtime"]]]

     [:div.Home__section
      [:div.Home_section--side
       [:h2 "Prototype Quickly"]
       [:p "Our easy to use interface makes it easy to work out ideas and makes it simple to change them as requirements change."]]
      [:div.Home_section--side
       [:img {:src "/images/placeholder.jpg" :width "250px"}]]]



     [:div.Home__lgSection
      [:div.Home__lgSection--side--left
       [:h2 "Feature Filled"]]
      [:div.Home__lgSection--side--right
       [:div.Home__feature
        [:div.Home__feature__single
         [:h3 "Asset Mananger"]
         [:p "Here is some quick text describing the feature and why it's a cool!"]]
        [:div.Home__feature__single
         [:h3 "Branching Paths"]
         [:p "Here is some quick text describing the feature and why it's a cool!"]]]
       [:div.Home__feature
        [:div.Home__feature__single
         [:h3 "Visualize at glance"]
         [:p "Here is some quick text describing the feature and why it's a cool!"]]
        [:div.Home__feature__single
         [:h3 "Conquer Complexity"]
         [:p "Here is some quick text describing the feature and why it's a cool!"]]]]]


     [:div.Home__ribbon.Home__ribbon-highlight
      [:h2 "Start Your Free Account"]
      [:button "Start Free - No Credit Card"]]

     [:div.Home__pricing
      [:h2 "Start For Free - Upgrade Whenever"]

      [:div.Home__pricing__options
       [:div.Home__pricing__card
        [:h3 "Free Plan"]
        [:div.Home__pricing__card__inner
         [:p "1 Project"]
         [:p "50 details"]
         [:p "- Unlimited Users"]
         [:p "- Limited Media Storage"]
         [:button "Sign Up Free"]]]
       [:div.Home__pricing__card
        [:h3 "$9/Month"]
        [:div.Home__pricing__card__inner
         [:p "- Unlimited Users"]
         [:p "- Unlimited Projects"]
         [:p "- Exta Media Storage"]
         [:p "- All Future Updates"]
         [:button "Start Free"]]]]]

     [:div.Home__footer
      [:ul
       [:li "Terms"]
       [:li "Privacy"]
       [:li "Copyright"]]]]))




