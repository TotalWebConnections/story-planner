(ns story-planner.server.services.database.hashers
  (:import  java.security.MessageDigest))

(defn sha256 [string]
  (let [digest (.digest (MessageDigest/getInstance "SHA-256") (.getBytes string "UTF-8"))]
    (apply str (map (partial format "%02x") digest))))
