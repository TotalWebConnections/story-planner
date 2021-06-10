(ns story-planner.views.Home_page
  (:require [reagent.core :as reagent :refer [atom]]
            [reitit.frontend.easy :as rfe]
            [story-planner.services.scripts.navigation :refer [home-scroll]]
            [story-planner.components.Terms :refer [Terms]]))


(defn Home-page []
  (let [show-terms (atom false)]
    (fn []
      [:div.Home
       (Terms @show-terms #(reset! show-terms false))
       [:div.Home__header
        [:div.Home__header__inner
         [:div.Home__header__block
          [:h1 {:on-click #(rfe/push-state :home)} "Narrative Planner"]
          [:ul.navList.noMobile
           [:li {:on-click #(home-scroll "pricing")} "Pricing"]
           [:li {:on-click #(home-scroll "features")} "Features"]
           [:li {:on-click #(home-scroll "contact")}  "Contact"]]]
         [:div.Home__header__block.Home__header__block--nav
          [:ul
           (if (.getItem js/localStorage "story-planner-token")
             [:li [:a {:href "/projects"} [:button.small "My Projects"]]]
             [:li [:button.small {:on-click #(rfe/push-state :signup)} "Sign up"]])
           (if-not (.getItem js/localStorage "story-planner-token")
             [:li {:on-click #(rfe/push-state :login)} "Login"])]]]]

       [:div.Home__mainVisual
        [:div.Home__mainVisual__inner
         [:div.Home__mainVisual-left
          [:h1 "Collaborative Design Made Easy"]
          [:p "Visualize and design complex paths collaboratively!"]
          [:div
           [:button {:on-click #(rfe/push-state :signup)} "Get Started For Free"]]]
         [:div.Home__mainVisual-right
          [:div.Home__mainVisual-right__imageContainer
           [:img.border {:src "/images/demo.jpg" :width "100%"}]
           [:div.Rectangle-block]]]]]
         ; [:img.Home__mainVisual__dots.flair {:src "/images/home/dots.svg"}]]]

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

       [:div.Home__sectionPadding {:id "features"}
        [:div.Home__section
         [:div.Home__section--side
          [:div
           [:h2.larger.grad1 "Visualize Complexity"]
           [:p "Easily visualize complex flows. Perfect for branching dialogue, modeling user decisions, or telling interactive stories."]]]
         [:div.Home__section--side
          [:div.imageContainer
           [:img {:src "/images/visualize_complexity.jpg" :width "100%"}]
           [:div.oval.firstOval]]]]]

       [:div.Home__sectionPadding.alternate
        [:div.Home__section
         [:div.Home__section--side
          [:div.imageContainer
           [:img.border {:src "/images/user_example.jpg" :width "100%"}]
           [:div.oval.secondOval]]]
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
          [:div.imageContainer
           [:img.border {:src "/images/easy_to_use.jpg" :width "100%"}]
           [:div.oval.thirdOval]]]]]



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

       [:div.Home__pricing {:id "pricing"}
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
           [:button.reverse {:on-click #(rfe/push-state :signup)} "Sign Up"]]]
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
           [:button.reverse {:on-click #(rfe/push-state :signup)} "Start Trial"]]]]

        [:div.Home__pricing__callout
         [:h2 "Start Your Free Account"]
         [:p "Get started free and upgrade at any time!"]
         [:button {:on-click #(rfe/push-state :signup)} "Start Free - No Credit Card"]]]

       [:div.Home__contact.Home__sectionPadding {:id "contact"}
        [:h2 "Question, Comment. Suggestion?"]
        [:p "We always love to hear from our users. If you have feedback we want to hear it! From issues you're having to feature suggestions send us an email and we'll get back to you."]
        [:h3 [:a {:href "mailto:support@narrativeplanner.com?subject = Narrative Planner Application"} "Support@narrativeplanner.com"]]]

       [:div.Home__footer
         [:div.Home__footer__inner
          [:div.Home__footer__section.smallFooterSection
           [:h3 "NarrativePlanner"]
           [:p.copyright "Copyright 2021 Total Web Connections LLC"]
           [:ul
            [:li {:on-click #(reset! show-terms true)} "Terms"]
            [:li "Privacy"]]]
          [:div.Home__footer__section.largeFooterSection
           [:div.Home__footer__subSection
            [:p.text-bold "Quick Links"]
            [:ul
             [:li {:on-click #(home-scroll "pricing")} "Pricing"]
             [:li {:on-click #(home-scroll "features")} "Features"]
             [:li {:on-click #(home-scroll "contact")}  "Contact"]]]
           [:div.Home__footer__subSection
            [:p.text-bold "Resources"]
            [:ul
             [:li "Help Articles"]
             [:li "Company"]]]]
          [:div.Home__footer__section.smallFooterSection
           [:p.text-bold "Get In touch"]
           [:p [:a {:href "mailto:support@narrativeplanner.com?subject = Narrative Planner Application"} "support@narrativeplanner.com"]]
           [:p [:a {:href "https://discord.gg/6NmHTSFb" :target "_blank"} "Discord"]]]]]])))



