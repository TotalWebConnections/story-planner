(ns story-planner.services.state.global
    (:require [reagent.core :as reagent :refer [atom]]))

; Define the state we need to hold here
; probably send our WS here too
(defonce app-state (atom {:canvasLoaded false ; prevents the canvas from reloading
                          :userToken "" ; string user login token for auth
                          :navType "app" ; Either app or view - display or not dispaly nav
                          :projects nil
                          :currentProject nil ; id of the current project opened
                          :board nil ; {:id :name} of the current board
                          :boardFolders nil ; [{}] vector of folder maps
                          :entityFolders nil
                          :entities nil ; [{}] vector of maps representing different entities of the board
                          }))


(defn get-from-state [key]
  "returns the value for a specified key from state"
  ((keyword key) app-state))

