(ns story-planner.services.state.global
    (:require [reagent.core :as reagent :refer [atom]]))

; Define the state we need to hold here
; probably send our WS here too
(defonce app-state (atom {:canvasLoaded false ; prevents the canvas from reloading
                          :dragId nil ; we use this to update the entity after a drag
                          :loginError nil
                          :sidebarActive nil
                          :user nil
                          :authorizedUsers []
                          :navType "app" ; Either app or view - display or not dispaly nav
                          :images [] ; For testing
                          :media-folders []
                          :show-media false
                          :app-show-media-manager false ; specific for the app page
                          :edited-storypoint nil ; used when selecting an image for a sp - since we woudln't know in the media manager which we're at
                          :currentProject nil ; id of the current project opened
                          :currentBoard nil ; currently selected board
                          :openedFolders {} ; currently opened folders to check - probably n^2 may need better implementation
                          :storypoints nil ; holds a reference to the details of the current project
                          :linkStartId nil}))
                            ; i.e all the actual entties that are placed in a particular board - this is separate from the projects I think??



(defn get-from-state [key]
  "returns the value for a specified key from state"
  ((keyword key) @app-state))

(defn get-current-user-token []
  "used in most api calls so we'll expose it on it's own"
  (:token (:user @app-state)))

