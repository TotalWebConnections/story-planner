(ns story-planner.components.app.header
  (:require [story-planner.services.scripts.navigation :refer [navigate]]
            [story-planner.services.scripts.trial :as trial-checks]
            [story-planner.services.state.global :refer [get-from-state]]))

; TODO navigate back to projects needs to clear data from currentProject
; and things like open folder/board ect.
(defn Header [projectName]
  [:div.Header
    [:div.Header__left
      [:div.Header__block
        [:p projectName]]
      (if (not (:subToken (get-from-state "currentProject")))
        [:div.Header__block
         [:div.Header__block__trialIndicator
          [:div.trialIndicator--inner {:style {:background "gold" :width (str (* 2 (trial-checks/check-total-usage)) "px")}}]]
         [:p.trialIndicator--text (str (trial-checks/check-total-usage) " of " trial-checks/MAX_ITEMS)]])]
    [:div.Header__right
      [:div.Header__block.nav
        [:p {:on-click #((navigate "projects"))} "Projects"]
        [:p {:on-click #((navigate "profile"))} "Account"]]]])
