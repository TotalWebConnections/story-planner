(ns story-planner.views.Home_page
  (:require [story-planner.services.scripts.navigation :refer [navigate]]))


(defn Home-page []
  (fn []
    [:div.Home
     [:div.Home__header
      [:div.Home__header__block
       [:h2 "Story Planner"]]
      [:div.Home__header__block
       [:ul
          [:li "Pricing"]
          [:li "Features"]
          [:li "Contact"]
        (if (.getItem js/localStorage "story-planner-token")
          [:li {:on-click #(navigate "projects")} "Projects"]
          [:span
            [:li {:on-click #(navigate "signup")} "Sign up"]
            [:li {:on-click #(navigate "login")} "Login"]])]]]



     [:div.Home__mainVisual
      [:div.Home__mainVisual__inner
       [:div.Home__mainVisual-left
        [:h1 "Collaborative Design Made Easy"]
        [:p "Visualize and design complex paths collaboratively!"]
        [:div
         [:button {:on-click #(navigate "signup")} "Get Started For Free"]]]
       [:div.Home__mainVisual-right
        [:img.border {:src "/images/demo.jpg" :width "100%"}]]]]

     [:div.Home__ribbon
      [:h2 "The Pefect Solution For"]
      [:div.Home__ribbon__wrapper
       [:div
        [:h3 "Game Designers"]
        [:img {:src "/images/icons/game_designers.png" :width "75%"}]]
       [:div
        [:h3 "Software Developers"]
        [:img {:src "/images/icons/software_developers.png" :width "75%"}]]
       [:div
        [:h3 "Writers"]
        [:img {:src "/images/icons/writers.png" :width "75%"}]]
       [:div
        [:h3 "RPG Players"]
        [:img {:src "/images/icons/rpg_players.png" :width "75%"}]]]]

     [:div.Home__section
      [:div.Home__section--side
       [:div
        [:h2 "Visualize Complexity"]
        [:p "Easily visualize complex flows. Perfect for branching dialogue, modeling user decisions, or telling interactive stories."]]]
      [:div.Home__section--side
       [:img.border {:src "/images/visualize_complexity.jpg" :width "100%"}]]]

     [:div.Home__section.alternate
      [:div.Home__section--side
       [:img.border {:src "/images/user_example.jpg" :width "100%"}]]
      [:div.Home__section--side
       [:div
        [:h2 "Collaborate Efficiently"]
        [:p "Share your project with co-workers and friends and seamlessly work togather on the same story in realtime. You have full control over who can access all your projects and can add/remove as many collaborators as you need."]]]]

     [:div.Home__section
      [:div.Home__section--side
       [:div
        [:h2 "Prototype Quickly"]
        [:p "Our easy to use interface makes it easy to work out ideas and makes it simple to change them as requirements change."]]]
      [:div.Home__section--side
       [:img.border {:src "/images/easy_to_use.jpg" :width "100%"}]]]



     [:div.Home__lgSection
      [:div.Home__lgSection--side--left
       [:div
        [:h2 "Feature Filled"]
        [:p "Already packed with time saving features, and we're constantly adding more!"]]]
      [:div.Home__lgSection--side--right
       [:div.Home__feature
        [:div.Home__feature__single
         [:h3 "Asset Mananger"]
         [:p "Easily organize image assets across multiple projects. A dedicated media manager helps you make sense of your visual content."]]
        [:div.Home__feature__single
         [:h3 "Branching Paths"]
         [:p "Quickly create and test branching story paths. Paths are simple to create and remove allowing you to quickly test new ideas collaboratively."]]]
       [:div.Home__feature
        [:div.Home__feature__single
         [:h3 "Visualize at glance"]
         [:p "Easily zoom out to view your whole flow or zoom in on specific details. Breakdown larger stories into boards to further help stay organized."]]
        [:div.Home__feature__single
         [:h3 "Conquer Complexity"]
         [:p "All the tools come together to help you build and manage complex, branching story flows. Master complexity and spend more time building and less time planning."]]]]]


     [:div.Home__ribbon.Home__ribbon-highlight
      [:h2 "Start Your Free Account"]
      [:p "Get started free and upgrade at any time!"]
      [:button {:on-click #(navigate "signup")} "Start Free - No Credit Card"]]

     [:div.Home__pricing
      [:h2 "Start For Free - Upgrade Whenever"]

      [:div.Home__pricing__options
       [:div.Home__pricing__card
        [:h3 "Free Plan"]
        [:div.Home__pricing__card__inner
         [:p "- 1 Project"]
         [:p "- 50 details"]
         [:p "- 1 User"]
         [:p "- Limited Media Storage"]
         [:button {:on-click #(navigate "signup")} "Sign Up Free"]]]
       [:div.Home__pricing__card
        [:h3 "$9/Month"]
        [:div.Home__pricing__card__inner
         [:p "- Unlimited Users"]
         [:p "- Unlimited Projects"]
         [:p "- Exta Media Storage"]
         [:p "- All Future Updates"]
         [:button {:on-click #(navigate "signup")} "Start Free"]]]]]

     [:div.Home__footer
      [:ul
       [:li "Terms"]
       [:li "Privacy"]]
      [:p.copyright "Copyright 2021 Total Web Connections LLC"]]]))




