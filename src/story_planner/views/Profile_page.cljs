(ns story-planner.views.Profile_page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.dom :as gdom]
            [cljs-http.client :as http]
            [story-planner.services.scripts.navigation :refer [navigate]]
            [story-planner.services.scripts.api.localstorage :refer [update-localstorage-by-key]]
            [story-planner.components.profile.registed_users :refer [Registered-users]]))


(defn handle-subscribe [stripe-token]
  "Takes are new stripe token and sends it to server to finish the subscription process"
  (go (let [response (<! (http/post "http://localhost:8080/subscribe"
                                 {:with-credentials? false
                                  :form-params {:token (:token (js->clj (js/JSON.parse (.getItem js/localStorage "story-planner-token")) :keywordize-keys true)) :stripeToken (:id (js->clj stripe-token :keywordize-keys true))}}))
            response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
          (if (= (:type response-body) "error")
            (js/alert "There Was an Error Processing Your Payment")
            (update-localstorage-by-key "subToken" (:data response-body))))))

(defn handle-unsubscribe [token sub-token]
  (go (let [response (<! (http/post "http://localhost:8080/unsubscribe"
                                 {:with-credentials? false
                                  :form-params {:token token :sub-token sub-token}}))
            response-body (js->clj (js/JSON.parse (:body response)) :keywordize-keys true)]
          (if (= (:type response-body) "error")
            (js/alert "Error - Please contact support")
            (update-localstorage-by-key "subToken" nil)))))

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
  (let [stripe (.Stripe js/window "pk_test_LgROF2ukcNIc3P3I3p4Nq31v") ;TODO we need to build this into a compile time var
        elements (.elements stripe)
        card (.create elements "card" (clj->js {:style card-style}))
        token (:token (:user @app-state))
        on-subscribe-error (atom false)]
    (js/setTimeout #(setup-card-handlers stripe card) 2000) ; TODO make this better
    (fn [app-state]
      [:div.Profile
        [:div.Profile__header
         [:h2 "Account"]
         [:div.Profile__header__nav
          [:p {:on-click #(navigate "projects")} "Projects"]]]
        [:div.Profile__inner
          [:h1 "My Details"]
          [:div.Profile__row
           [:h2 "Billing"]
           (if (not (:subToken (:user @app-state)))
             [:div.Profile__subscribe
              [:p "You are currently not subscibed. Filling out the form below you will be charged $9/month until you cancel."]
              [:form#subscription-form {:action "/subscribe" :method "post"}
               [:div#card-element]
               [:div#card-errors]
               [:button.subscribe_button {:type "submit"} "Subscribe"]]]
             [:div.Profile__subscribe
              [:p "You are currently subscribed! If you wish to cancel please click the button below to immediately cancel your subscriptions and stop future payments. You will not receive a refund for any unused time in your account."]
              [:button.subscribe_button {:on-click #(handle-unsubscribe token (:subToken (:user @app-state)))} "Cancel Subscription"]])]
          [:div.Profile__row
           [Registered-users (:projects @app-state) (:users @app-state)]]]])))
