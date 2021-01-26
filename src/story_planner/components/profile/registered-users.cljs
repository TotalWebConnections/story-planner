(ns story-planner.components.profile.registed_users
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
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


(defn add-user-to-project [project userId authorizedUsers]
  (let [newAuthUsers (conj authorizedUsers userId)]
    (handle-state-change {:type "update-project-authorized-users" :value {:projectId (:_id project) :authorizedUsers newAuthUsers}})
    (api/udpdate-project-permissions {:authorizedUsers newAuthUsers :projectId (:_id project)})))

(defn remove-user-from-project [project userId authorizedUsers]
  (let [newAuthUsers (filter #(not (= % userId)) authorizedUsers)]
    (handle-state-change {:type "update-project-authorized-users" :value {:projectId (:_id project) :authorizedUsers newAuthUsers}})
    (api/udpdate-project-permissions {:authorizedUsers newAuthUsers :projectId (:_id project)})))

(defn update-project-permissions [e project userId]
  (let [authorizedUsers (:authorizedUsers project)]
    (if (.-checked (.-target e))
      (add-user-to-project project userId authorizedUsers)
      (remove-user-from-project project userId authorizedUsers))))

(defn Registered-users [projects auth-users]
  (let [added-user (atom {:name nil :email nil})
        initial-add-list (atom [])]
    (fn [projects auth-users]
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
                 [:th {:key (:_id project)} (:name project)])]
              [:tr
               (for [project projects]
                 [:td {:key (:_id project)}
                  [:input {:type "checkbox" :id (:_id project) :on-click #(handle-new-user-checkbox % initial-add-list)}]])]]]])]
        [:button {:on-click #(handle-add-authorized-user added-user initial-add-list)} "Add User"]]
       [:div.RegisteredUsers__section
        [:div.RegisteredUsers__header
         [:h3 "Current Users"]
         [:h3.visibleProjects "Visible Projects"]]
        [:table.RegisteredUsers__currentWrapper
         [:tbody
          [:tr
           [:th.userTableCell "Name"]
           (for [project projects]
             [:th {:key (:_id project)} (:name project)])
           [:t.userTableCell ""]]
          (for [user auth-users]
            [:tr {:key (:_id user)}
             [:td.userTableCell (:name user)]
             (for [project projects]
               [:td {:key (:_id project)}
                [:input {:type "checkbox"
                         :checked (some #(= (:_id user) %) (:authorizedUsers project))
                         :on-change #(update-project-permissions % project (:_id user))}]])
             [:td [:button {:on-click #(api/delete-authorized-user {:userId (:_id user)})} "Delete"]]])]]]])))
