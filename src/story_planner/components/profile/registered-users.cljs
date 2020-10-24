(ns story-planner.components.profile.registed_users
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]))

(defn handle-add-authorized-user [added-user initial-add-list]
  (api/add-new-authorized-user {:user @added-user :projectIds @initial-add-list})
  (swap! added-user conj {:name nil :email nil})
  (reset! initial-add-list []))


(defn handle-new-user-checkbox [e initial-add-list]
  (if (.-checked (.-target e))
    (swap! initial-add-list conj (.-id (.-target e)))
    (swap! initial-add-list (fn [projects]
                              (into [] (remove #(= % (.-id (.-target e))) projects))))))
  ; (print @initial-add-list))

(defn Registered-users [projects auth-user]
  (let [added-user (atom {:name nil :email nil})
        initial-add-list (atom [])]
    (fn [projects auth-user]
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
             [:tbody
              [:tr
               (for [project projects]
                 [:th (:name project)])]
              [:tr
               (for [project projects]
                 [:td
                  [:input {:type "checkbox" :id (:_id project) :on-click #(handle-new-user-checkbox % initial-add-list)}]])]]]])]
        [:button {:on-click #(handle-add-authorized-user added-user initial-add-list)} "Add User"]]
       [:div.RegisteredUsers__section
        [:h3 "Current Users"]
        [:table.RegisteredUsers__currentWrapper
         [:tbody
          [:tr
           [:th.userTableCell "Name"]
           (for [project projects]
             [:th (:name project)])]
          (for [user auth-user]
            [:tr
             [:td.userTableCell (:name user)]
             (for [project projects]
               [:td
                [:input {:type "checkbox" :checked (some #(= (:_id project) %) (:access user))}]])])]]]])))
