(ns story-planner.server.services.user
  (:require [clojure.spec.alpha :as s]
            [cheshire.core            :refer :all]
            [story-planner.server.services.response-handler :refer [wrap-response]]
            [story-planner.server.services.database.users :as DB-users]
            [story-planner.server.services.database.authorized :as DB-auth-users]
            [story-planner.server.services.billing :refer [create-new-customer stripe-unsubscribe-user]]
            [buddy.hashers :as hashers]))

(s/def ::email (s/and string? (partial re-matches #"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,63}$")))
(s/def ::password-not-empty not-empty)
(s/def ::password-equal (fn [{:keys [password password-repeat]}]
                           (= password password-repeat)))

;TODO add email check duplicate
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

(defn handle-save-user [user]
  (let [error-block (validate-input user)]
    (if (> (count error-block) 0)
      (wrap-response "error" error-block)
      (try
        (wrap-response "success"
          (DB-users/add-user
            (dissoc
              (conj user {:password (hashers/derive (:password user) {:alg :bcrypt+blake2b-512})}) :password-repeat)))
        (catch Exception e
          (wrap-response "error" "Email Is Already In Use"))))))

(defn handle-login-user [user-creds]
  (let [user (DB-users/get-user (:email user-creds))]
    (if (and user (:valid (hashers/verify (:password user-creds) (:password user))))
      (wrap-response "success" (dissoc (conj user {:token (DB-users/update-user-token (:email user-creds))}) :password :parentId)) ;do update token send to ui
      (wrap-response "error" "Password Error"))))

(defn validate-token [id token]
  (DB-users/check-user-token id token))

(defn validate-token-return-user [id token]
  (DB-users/get-user-by-token id token))

(defn check-user-token [id token]
  (wrap-response "success" (validate-token id token)))

(defn handle-subscribe-success [user-token sub-token])

; (:id (first (:data (:subscriptions stripeResult))))
(defn handle-subscribe-user [token email id]
  (let [stripe-result (create-new-customer token email)
        sub-token (:id (first (:data (:subscriptions stripe-result))))]
    (if sub-token
      (DB-users/add-user-stripe-token sub-token id)
      (wrap-response "error" "Token invalid"))))

(defn subscribe-user [values]
  (let [user (validate-token-return-user (:_id values) (:token values))]
    (if user ; will be false if the token doesn't exist
      (wrap-response "success" (handle-subscribe-user (:stripeToken values) (:email user) (:_id user))) ; TODO make the real email
      (wrap-response "error" "Token invalid"))))

(defn handle-unsubscribe-user [user-token sub-token]
  (stripe-unsubscribe-user sub-token)
  (wrap-response "success" (DB-users/add-user-stripe-token nil user-token)))

(defn unsubscribe-user [values]
  (if (validate-token (:token values))
    (wrap-response "success" (handle-unsubscribe-user (:token values) (:sub-token values)))
    (wrap-response "error" "Token invalid")))

(defn signup-auth-user [values]
  (if (DB-auth-users/user-with-token-exists? (:id values))
    (wrap-response "success" (DB-auth-users/update-auth-user (:id values) (hashers/derive (:password values) {:alg :bcrypt+blake2b-512})))
    (wrap-response "error" "Token invalid")))


