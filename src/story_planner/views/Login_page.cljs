(ns story-planner.views.Login_page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [reitit.frontend.easy :as rfe]
            [story-planner.services.scripts.api.localstorage :refer [update-local-storage]]
            [cljs-http.client :as http]
            [story-planner.config :refer [api]]))

(defn login-success [data]
  (update-local-storage data)
  (rfe/push-state :projects))

(defn handle-login [user errors]
  (reset! errors nil)
  (go (let [response (<! (http/post (str api "/login")
                                 {:with-credentials? false
                                  :form-params {:email (:email @user) :password (:password @user)}}))
            response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
        (if (= (:type response-body) "error")
          (reset! errors (:data response-body))
          (login-success (:data response-body))))))

(defn Login-page [app-state]
  (let [user (atom {:email "" :password ""})
        errors (atom nil)]
    (fn []
      [:div.Login
       [:div.Login__header.standard-padding
         [:h2 {:on-click #(rfe/push-state :home)} "Narrative Planner"]]
       [:div.Login__inner
        [:div.Login__form
         [:h2.noBottomMargin "Login"]
         (if @errors [:p.ErrorText "Email or Password Invalid"])
         (if (:loginError @app-state) [:p.ErrorText (:loginError @app-state)])
         [:input {:type "text" :placeholder "email" :on-change #(swap! user conj {:email (-> % .-target .-value)})}]
         [:input {:type "password" :placeholder "password" :on-change #(swap! user conj {:password (-> % .-target .-value)})}]
         [:button  {:on-click #(handle-login user errors)}"Submit"]]
        [:p.Login__inner__switcher {:on-click #(rfe/push-state :signup)} "No Account? Create One Free!"]]])))
