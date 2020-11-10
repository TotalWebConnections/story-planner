(ns story-planner.views.Login_page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.navigation :refer [navigate]]
            [story-planner.services.scripts.api.localstorage :refer [update-local-storage]]
            [cljs-http.client :as http]
            [story-planner.services.scripts.navigation :refer [navigate]]))

(defn login-success [data]
  (update-local-storage data)
  (navigate "projects"))

(defn handle-login [user errors]
  (reset! errors nil)
  (go (let [response (<! (http/post "http://localhost:8080/login"
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
         [:h2 {:on-click #((navigate ""))} "App Name"]]
       [:div.Login__inner
        [:div.Login__form
         [:h1 "Login"]
         (if @errors [:p.ErrorText "Email or Password Invalid"])
         (if (:loginError @app-state) [:p.ErrorText (:loginError @app-state)])
         [:input {:type "text" :placeholder "email" :on-change #(swap! user conj {:email (-> % .-target .-value)})}]
         [:input {:type "password" :placeholder "password" :on-change #(swap! user conj {:password (-> % .-target .-value)})}]
         [:button  {:on-click #(handle-login user errors)}"Submit"]]]])))
