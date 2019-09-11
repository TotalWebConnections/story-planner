(ns story-planner.services.state.global
    (:require [reagent.core :as reagent :refer [atom]]))

; Define the state we need to hold here
; probably send our WS here too
(defonce app-state (atom {:canvasLoaded false ; prevents the canvas from reloading
                          :userToken "" ; string user login token for auth
                          :navType "app" ; Either app or view - display or not dispaly nav
                          :currentProject nil ; id of the current project opened
                          :currentProjectDetails nil ; holds a reference to the details of the current project
                            ; i.e all the actual entties that are placed in a particular board - this is separate from the projects I think??
                          }))


(defn get-from-state [key]
  "returns the value for a specified key from state"
  ((keyword key) app-state))

