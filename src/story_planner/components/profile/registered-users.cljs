(ns story-planner.components.profile.registed_users
  (:require [reagent.core :as reagent :refer [atom]]))

; TODO for testing - remove
(def authUsers
  [{:name "Peter" :id "123" :access [1 4]}
   {:name "Bobby Hill" :id "234" :access [4 2]}])

(def projects
  [{:name "test" :id 1}
   {:name "test1" :id 2}
   {:name "test2" :id 3}
   {:name "test3" :id 4}
   {:name "test4" :id 5}])

(defn Registered-users []
  (let [added-user (atom {:name nil :email nil})]
    (fn []
      [:div.RegisteredUsers
       [:h2 "My Team"]
       [:p "Invite team members by email and manage which projects they have access to."]
       [:div.RegisteredUsers__section
        [:h3 "Add a New User"]
        [:div.RegisteredUsers__inputWrapper
         [:input {:type "text" :placeholder "name" :on-change #(swap! added-user conj {:name (-> % .-target .-value)})}]
         [:input {:type "text" :placeholder "email" :on-change #(swap! added-user conj {:email (-> % .-target .-value)})}]
         (if (:name @added-user)
           [:div
            [:h4 "Select which projects to grant access"]
            [:table.RegisteredUsers__currentWrapper
             [:tr
              (for [project projects]
                [:th (:name project)])]
             [:tr
              (for [project projects]
                [:td
                 [:input {:type "checkbox"}]])]]])]]
       [:div.RegisteredUsers__section
        [:h3 "Current Users"]
        [:table.RegisteredUsers__currentWrapper
         [:tr
          [:th.userTableCell "Name"]
          (for [project projects]
            [:th (:name project)])]
         (for [user authUsers]
           [:tr
            [:td.userTableCell (:name user)]
            (for [project projects]
              [:td
               [:input {:type "checkbox" :checked (some #(= (:id project) %) (:access user))}]])])]]])))
