(ns story-planner.views.Signup_page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.spec.alpha :as s]
            [story-planner.services.scripts.api.localstorage :refer [update-local-storage]]
            [story-planner.services.scripts.navigation :refer [navigate]]
            [story-planner.config :refer [api]]
            [cljs-http.client :as http]))

; TODO this whole logic is shared on the backend as well, we should probably make this into a shareable file to write onc
(s/def ::email (s/and string? (partial re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")))
(s/def ::password-not-empty not-empty)
(s/def ::password-equal (fn [{:keys [password confirm]}]
                           (= password confirm)))

(defn validate-email [email]
  (if (s/valid? ::email email)
    nil
    {:email "Email is Invalid"}))

(defn validate-password-empty [password]
  (if (s/valid? ::password-not-empty password)
    nil
    {:password "Password is Empty"}))

(defn validate-password-equal [user]
  (if (s/valid? ::password-equal user)
    nil
    {:password-confirm "Passwords Don't Match"}))

(defn validate-input [user]
  (merge (validate-email (:email user))
         (validate-password-empty (:password user))
         (validate-password-equal user)))

(defn signup-success [data]
  (update-local-storage data)
  (navigate "projects"))

(defn handle-signup [user errors]
  (reset! errors nil)
  (let [error-block (validate-input @user)]
    (if (> (count error-block) 0)
      (reset! errors error-block)
      (go (let [response (<! (http/post (str api "/user")
                                     {:with-credentials? false
                                      :form-params {:email (:email @user) :password (:password @user) :password-repeat (:confirm @user)}}))
                response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
            (if (= (:type response-body) "error")
              (reset! errors (:data response-body))
              (signup-success (:data response-body))))))))


(defn Signup-page []
  (let [user (atom {:email "" :password "" :confirm ""})
        errors (atom nil)]
    (fn []
      [:div.Signup
       [:div.Signup__header.standard-padding
         [:h2 {:on-click #((navigate ""))} "Narrative Planner"]]
       [:div.Signup__inner
        [:div.Signup__form
         [:h1 "Signup"]
         (if (:email @errors) [:p.ErrorText (:email @errors)])
         [:input {:type "text" :placeholder "email" :on-change #(swap! user conj {:email (-> % .-target .-value)})}]
         (if (:password @errors) [:p.ErrorText (:password @errors)])
         [:input {:type "password" :placeholder "password" :on-change #(swap! user conj {:password (-> % .-target .-value)})}]
         (if (:password-confirm @errors) [:p.ErrorText (:password-confirm @errors)])
         [:input {:type "password" :placeholder "confirm password" :on-change #(swap! user conj {:confirm (-> % .-target .-value)})}]
         [:button  {:on-click #(handle-signup user errors)}"Sign Up"]]
        [:p.Signup__inner__switcher {:on-click #((navigate "login"))} "Already Have an Account?"]]])))
