(ns story-planner.server.services.billing
  (:require [config.core :refer [env]]
            [clj-http.client :as client]
            [clj-stripe.util :as util]
            [clj-stripe.common :as common]
            [clj-stripe.plans :as plans]
            [clj-stripe.coupons :as coupons]
            [clj-stripe.charges :as charges]
            [clj-stripe.cards :as cards]
            [clj-stripe.subscriptions :as subscriptions]
            [clj-stripe.customers :as customers]
            [clj-stripe.invoices :as invoices]
            [clj-stripe.invoiceitems :as invoiceitems]))

(defn create-new-customer [stripeToken email]
  (common/with-token (:stripeprivatekey env)
    (common/execute (customers/create-customer
                     (common/card stripeToken)
                     (customers/email email)
                     (common/plan (:stripeplan env))))))


(defn stripe-unsubscribe-user
  "Unsubscribes a user from stripe"
  [subToken]
  (let [url (str "https://api.stripe.com/v1/subscriptions/" subToken)]
    (client/delete url {:basic-auth (:stripeprivatekey env)})))
