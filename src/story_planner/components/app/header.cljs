(ns story-planner.components.app.header
  (:require [story-planner.services.scripts.navigation :refer [navigate]]))

; TODO navigate back to projects needs to clear data from currentProject
; and things like open folder/board ect.
(defn Header [projectName]
  [:div.Header
    [:div.Header__left
      [:div.Header__block
        [:p projectName]]]
    [:div.Header__right
      [:div.Header__block
        [:p {:on-click #((navigate ""))} "Projects"]
        [:p {:on-click #((navigate ""))} "Settings"]
        [:p {:on-click #((navigate ""))} "Account"]]]])