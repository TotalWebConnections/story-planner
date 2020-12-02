(ns story-planner.components.project.Confirmation)




(defn Confirmation [active text on-confirm on-cancel]
  [:div.Confirmation {:class active}
   [:div.Confirmation__inner
    [:p text]
    [:div.Confirmation__inner__buttons
     [:button {:on-click #(on-confirm)} "Confirm"]
     [:button {:on-click #(on-cancel)} "Cancel"]]]])
