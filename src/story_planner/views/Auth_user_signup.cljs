(ns story-planner.views.Auth_user_signup
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.config :refer [api]]
            [reitit.frontend.easy :as rfe]
            [story-planner.services.scripts.api.localstorage :refer [update-local-storage]]
            [cljs-http.client :as http]))

(defn on-success [response]
  (update-local-storage response)
  (rfe/push-state :projects))

(defn handle-login [id user errors]
  (reset! errors nil)
  (go (let [response (<! (http/post (str api "/signup-auth-user")
                                 {:with-credentials? false
                                  :form-params {:id id :password (:password @user)}}))
            response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
        (if (= (:type response-body) "error")
          (reset! errors (:data response-body))
          (on-success (:data response-body))))))

(defn Auth-user-page [app-state id]
  (let [user (atom {:password ""})
        errors (atom nil)]
    (fn []
      [:div.Login
       [:div.Login__header.standard-padding
         [:h2 {:on-click #(rfe/push-state :home)} "Narrative Planner"]]
       [:div.Login__inner
        [:div.Login__form
         [:h3 "Setup a Password For Your Account"]
         (if @errors [:p.ErrorText "Your Signup Link Is Invalid or Has Already Been Used"])
         (if (:loginError @app-state) [:p.ErrorText (:loginError @app-state)])
         [:input {:type "password" :placeholder "password" :on-change #(swap! user conj {:password (-> % .-target .-value)})}]
         [:button  {:on-click #(handle-login id user errors)}"Submit"]]]])))
