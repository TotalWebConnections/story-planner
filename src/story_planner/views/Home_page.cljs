(ns story-planner.views.Home_page
  (:require [story-planner.services.scripts.navigation :refer [navigate]]))


(defn Home-page []
  (fn []
    [:div.Home
     [:div.Home__header
      [:div.Home__header__block
       [:h1 "Narrative Planner"]]
      [:div.Home__header__block.Home__header__block--nav
       [:ul
          [:li "Pricing"]
          [:li "Features"]
          [:li "Contact"]
        (if (.getItem js/localStorage "story-planner-token")
          [:li [:button.small {:on-click #(navigate "projects")} "My Projects"]]
          [:li [:button.small {:on-click #(navigate "signup")} "Sign up"]])
        (if-not (.getItem js/localStorage "story-planner-token")
          [:li {:on-click #(navigate "login")} "Login"])]]]



     [:div.Home__mainVisual
      [:div.Home__mainVisual__inner
       [:div.Home__mainVisual-left
        [:h1 "Collaborative Design Made Easy"]
        [:p "Visualize and design complex paths collaboratively!"]
        [:div
         [:button {:on-click #(navigate "signup")} "Get Started For Free"]]]
       [:div.Home__mainVisual-right
        [:img.border {:src "/images/demo.jpg" :width "100%"}]]
       [:img.Home__mainVisual__dots {:src "/images/home/dots.svg"}]]]

     [:div.Home__ribbon
      [:h2 "The Pefect Solution For"]
      [:div.Home__ribbon__wrapper
       [:div
        [:img {:src "/images/home/game-developers.svg" :width "50%"}]
        [:h4 "Game Designers"]]
       [:div
        [:img {:src "/images/home/software-devs.svg" :width "50%"}]
        [:h4 "Software Developers"]]
       [:div
        [:img {:src "/images/home/rpg-players.svg" :width "60%"}]
        [:h4 "Writers"]]
       [:div
        [:img {:src "/images/home/rpg-players.svg" :width "60%"}]
        [:h4 "RPG Players"]]]]

     [:div.Home__sectionPadding
      [:div.Home__section
       [:div.Home__section--side
        [:div
         [:h2.larger.grad1 "Visualize Complexity"]
         [:p "Easily visualize complex flows. Perfect for branching dialogue, modeling user decisions, or telling interactive stories."]]]
       [:div.Home__section--side
        [:img.border {:src "/images/visualize_complexity.jpg" :width "100%"}]]]]

     [:div.Home__sectionPadding.alternate
      [:div.Home__section
       [:div.Home__section--side
        [:img.border {:src "/images/user_example.jpg" :width "100%"}]]
       [:div.Home__section--side
        [:div
         [:h2.larger.grad2 "Collaborate Efficiently"]
         [:p "Share your project with co-workers and friends and seamlessly work together on the same story in realtime. You have full control over who can access all your projects and can add/remove as many collaborators as you need."]]]]]

     [:div.Home__sectionPadding
      [:div.Home__section
       [:div.Home__section--side
        [:div
         [:h2.larger.grad3 "Prototype  Quickly"]
         [:p "Our easy to use interface makes it easy to work out ideas and makes it simple to update them as requirements change."]]]
       [:div.Home__section--side
        [:img.border {:src "/images/easy_to_use.jpg" :width "100%"}]]]]



     [:div.Home__lgSection
      [:div.Home__lgSection__header
        [:h2 "Feature Filled"]
        [:p "Already packed with time saving features, and we're constantly adding more!"]]
      [:div.Home__lgSection__featureWrap
       [:div.Home__feature
         [:img {:src "/images/home/asset-manager.svg" :width "34px"}]
         [:h3.Home__feature-header "Asset Mananger"]
         [:p "Easily organize image assets across multiple projects. A dedicated media manager helps you make sense of your visual content."]]
       [:div.Home__feature
         [:img {:src "/images/home/visualize.svg" :width "34px"}]
         [:h3.Home__feature-header "Branching Paths"]
         [:p "Quickly create and test branching story paths. Paths are simple to create and remove allowing you to quickly test new ideas collaboratively."]]

       [:div.Home__feature
         [:img {:src "/images/home/paths.svg" :width "50px"}]
         [:h3.Home__feature-header "Visualize at glance"]
         [:p "Easily zoom out to view your whole flow or zoom in on specific details. Breakdown larger stories into boards to further help stay organized."]]
       [:div.Home__feature
         [:img {:src "/images/home/complexity.svg" :width "34px"}]
         [:h3.Home__feature-header "Conquer Complexity"]
         [:p "All the tools come together to help you build and manage complex, branching story flows. Master complexity and spend more time building and less time planning."]]]]




     [:div.Home__pricing
      [:img.Home__pricing__topline {:src "/images/home/top-line.svg"}]
      [:img.Home__pricing__bottomline {:src "/images/home/bottom-line.svg"}]
      [:div.Home__pricing__header
       [:h2 "Start For Free - Upgrade When You Need"]]

      [:div.Home__pricing__options
       [:div.Home__pricing__card
        [:div.Home__pricing__card__header
         [:p.paraBold "Free Plan"]
         [:p.paraHuge "$0"]
         [:p.small "/month"]]
        [:div.Home__pricing__card__inner
         [:p "1 Project"]
         [:p "50 details"]
         [:p "1 User"]
         [:p "Limited Media Storage"]
         [:button.reverse {:on-click #(navigate "signup")} "Sign Up"]]]
       [:div.Home__pricing__card
        [:div.Home__pricing__card__header
         [:p.paraBold "Advanced Plan"]
         [:p.paraHuge "$9"]
         [:p.small "/month"]]
        [:div.Home__pricing__card__inner
         [:p "Unlimited Users"]
         [:p "Unlimited Projects"]
         [:p "Exta Media Storage"]
         [:p "All Future Updates"]
         [:button.reverse {:on-click #(navigate "signup")} "Start Trial"]]]]

      [:div.Home__pricing__callout
       [:h2 "Start Your Free Account"]
       [:p "Get started free and upgrade at any time!"]
       [:button {:on-click #(navigate "signup")} "Start Free - No Credit Card"]]]

     [:div.Home__footer
       [:div.Home__footer_section.smallFooterSection
        [:p.copyright "Copyright 2021 Total Web Connections LLC"]
        [:ul
         [:li "Terms"]
         [:li "Privacy"]]]
       [:div.Home__footer_section.largeFooterSection
        [:p "Links"]]
       [:div.Home__footer_section.smallFooterSection
        [:p "get In touch"]]]]))




