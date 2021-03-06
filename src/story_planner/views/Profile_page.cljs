(ns story-planner.views.Profile_page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.dom :as gdom]
            [story-planner.config :refer [stripe-public-key]]
            [cljs-http.client :as http]
            [reitit.frontend.easy :as rfe]
            [story-planner.config :refer [api]]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.components.Loader :refer [Loader]]
            [story-planner.services.scripts.api.localstorage :refer [update-localstorage-by-key delete-localstorage-val]]
            [story-planner.components.profile.registered-users :refer [Registered-users]]))

; We put this here as the stripe flow is a little different and it's just easier
(def on-subscribe-error (atom false))
(def on-unsubscribe-error (atom false))
(def is-handling-billing? (atom false))

(defn handle-on-sub-change [tokenVal isCancel?]
  (if isCancel?
    (js/alert "Your Subscription Was Succesfully Cancelled."))
  (update-localstorage-by-key "subToken" tokenVal)
  (reset! is-handling-billing? false))

(defn handle-subscribe [stripe-token]
  "Takes are new stripe token and sends it to server to finish the subscription process"
  (reset! is-handling-billing? true)
  (go (let [response (<! (http/post (str api "/subscribe")
                                 {:with-credentials? false
                                  :form-params {:_id (:_id (get-from-state "user")) :token (:token (js->clj (js/JSON.parse (.getItem js/localStorage "story-planner-token")) :keywordize-keys true)) :stripeToken (:id (js->clj stripe-token :keywordize-keys true))}}))
            response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
          (if (= (:type response-body) "error")
            (do
              (reset! is-handling-billing? false)
              (reset! on-subscribe-error "There Was an Error Processing Your Payment"))
            (handle-on-sub-change (:data response-body) false)))))

(defn handle-unsubscribe [token sub-token]
  (reset! is-handling-billing? true)
  (go (let [response (<! (http/post (str api "/unsubscribe")
                                 {:with-credentials? false
                                  :form-params {:_id (:_id (get-from-state "user")) :token token :sub-token sub-token}}))
            response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
          (if (= (:type response-body) "error")
            (do
              (reset! is-handling-billing? false)
              (reset! on-unsubscribe-error "Error - Please contact support"))
            (handle-on-sub-change nil true)))))

(def card-style {
                 :base {
                        :color "white"
                        :fontFamily "Helvetica Neue"
                        :fontSmoothing "antialiased"
                        :fontSize "16px"}


                 :invalid {
                            :color "#fa755a"
                            :iconColor "#fa755a"}})

(defn handle-on-card-change [event]
  (let [displayError (.getElementById js/document "card-errors")]
    (if (.-error event)
      (gdom/setTextContent displayError (-> event .-error .-message))
      (gdom/setTextContent displayError ""))))


(defn handle-form-submit [event stripe card]
  (.preventDefault event)
  (.then (.createToken stripe card)
    (fn [result]
      (if (.-error result)
        (handle-on-card-change result) ; we can reuse this as it's the same flow
        (handle-subscribe (.-token result))))))


(defn setup-on-form-submit [stripe card]
  (let [form (.getElementById js/document "subscription-form")]
    (.addEventListener form "submit" (fn [event] (handle-form-submit event stripe card)))))

(defn setup-card-handlers [stripe card]
  (.mount card "#card-element")
  (.on card "change" (fn [event] (handle-on-card-change event)))
  (setup-on-form-submit stripe card))



(defn Profile-page [app-state]
  (let [stripe (.Stripe js/window stripe-public-key)
        elements (.elements stripe)
        card (.create elements "card" (clj->js {:style card-style}))
        token (:token (:user @app-state))]
    (js/setTimeout #(setup-card-handlers stripe card) 1000) ; TODO make this better
    (fn [app-state]
      [:div.Profile
        (if @is-handling-billing?
          [Loader])
        [:div.Profile__header
         [:h2 "Account"]
         [:div.Profile__header__nav
          [:p {:on-click #(rfe/push-state :projects)} "Projects"]]]
        [:div.Profile__inner
          [:h1 "My Details"]
          [:p.logout {:on-click #(delete-localstorage-val)} "Logout"]
          [:div.Profile__row
           [:h2 "Billing"]
           (if (not (:subToken (:user @app-state)))
             [:div.Profile__subscribe
              [:p "You are currently not subscibed. Filling out the form below you will be charged $9/month until you cancel."]
              (if @on-subscribe-error
                [:p.ErrorText @on-subscribe-error])
              [:form#subscription-form {:action "/subscribe" :method "post"}
               [:div#card-element]
               [:div#card-errors]
               [:button.subscribe_button {:type "submit"} "Subscribe"]]]
             [:div.Profile__subscribe
              [:p "You are currently subscribed! If you wish to cancel please click the button below to immediately cancel your subscriptions and stop future payments. You will not receive a refund for any unused time in your account."]
              (if @on-unsubscribe-error
                [:p.ErrorText @on-unsubscribe-error])
              [:button.subscribe_button {:on-click #(handle-unsubscribe token (:subToken (:user @app-state)))} "Cancel Subscription"]])]
          [:div.Profile__row
           [Registered-users (:projects @app-state) (:users @app-state) (:subToken (:user @app-state))]]]])))
