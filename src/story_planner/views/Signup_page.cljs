(ns story-planner.views.Signup_page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.navigation :refer [navigate]]
            [cljs-http.client :as http]))

(defn handle-signup [user errors]
  (reset! errors nil)
  (go (let [response (<! (http/post "http://localhost:8080/user"
                                 {:with-credentials? false
                                  :form-params {:email (:email @user) :password (:password @user) :password-repeat (:confirm @user)}}))]
        (reset! errors (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)))))

(defn Signup-page []
  (let [user (atom {:email "" :password "" :confirm ""})
        errors (atom nil)]
    (fn []
      [:div.Signup
       [:div.Signup__header.standard-padding
         [:h2 {:on-click #((navigate ""))} "My Projects"]]
       [:div.Signup__inner
        [:div.Signup__form
         [:h1 "Signup"]
         (if (:email @errors) [:p.ErrorText (:email @errors)])
         [:input {:type "text" :placeholder "email" :on-change #(swap! user conj {:email (-> % .-target .-value)})}]
         (if (:password @errors) [:p.ErrorText (:password @errors)])
         [:input {:type "password" :placeholder "password" :on-change #(swap! user conj {:password (-> % .-target .-value)})}]
         (if (:password-confirm @errors) [:p.ErrorText (:password-confirm @errors)])
         [:input {:type "password" :placeholder "confirm password" :on-change #(swap! user conj {:confirm (-> % .-target .-value)})}]
         [:button  {:on-click #(handle-signup user errors)}"Submit"]]]])))
