(ns story-planner.server.services.user
  (:require [clojure.spec.alpha :as s]
            [cheshire.core            :refer :all]
            [clojure.walk :as walk]))

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
      (generate-string error-block)
      "Save Succesfull")))

