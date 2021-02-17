(ns story-planner.server.services.mail
  (:require [mailgun.mail :as mail]
            [config.core :refer [env]]
            [mailgun.util :refer [to-file]]))


(def BASE_URL "https://api.mailgun.net/v3/narrativeplanner.com")

(def EMAIL_HTML "
  <div>
     <h2>You've Been Invited!</h2>
     <p>You've been invited to work on a project in Narrative Planner. Please click or copy and paste
        the link below to set up your account.
     </p>
     <a href='https://www.narrativeplanner.com/signup-auth-user/KEY_REPLACE'>https://www.narrativeplanner.com/signup-auth-user/KEY_REPLACE</a>
  </div>")


(def creds {:key (:mailgunapikey env) :domain "narrativeplanner.com"})

(def content {:from "support@narrativeplanner.com"
              :to "EMAIL_HERE"
              :subject "Test"
              :html EMAIL_HTML})


(defn format-contents [content to key]
  (conj content {:to to
                 :html (clojure.string/replace (:html content) #"KEY_REPLACE" key)}))


(defn send-mail [to key]
  (mail/send-mail creds (format-contents content to key)))


