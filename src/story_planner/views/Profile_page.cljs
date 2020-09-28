(ns story-planner.views.Profile_page
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as reagent :refer [atom]]
            [goog.dom :as gdom]
            [story-planner.services.scripts.navigation :refer [navigate]]
            [cljs-http.client :as http]))

(defn handle-subscribe [token]
  "Takes are new stripe token and sends it to server to finish the subscription process"
  (print token))

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
        subscribed false]
    (js/setTimeout #(setup-card-handlers stripe card) 2000) ; TODO make this better
    (fn []
      [:div.Profile
        [:div.Profile__header.standard-padding
         [:h2 {:on-click #((navigate ""))} "App Name"]]
        [:div.Profile__inner
          [:h1 "My Details"]
          [:div.Profile__row
           [:h2 "Billing"]
           [:p "You are currently not subscibed. Filling out the form below you will be charged $9/month until you cancel."]
           [:form#subscription-form {:action "/subscribe" :method "post"}
            [:div#card-element]
            [:div#card-errors]
            [:button {:type "submit"} "Subscribe"]]]]])))
