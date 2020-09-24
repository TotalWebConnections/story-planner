(ns story-planner.services.state.dispatcher
  (:require [story-planner.services.state.global :refer [app-state]]
            [story-planner.services.state.textstate :refer [update-state-text]]
            [story-planner.services.state.actions.folders :refer [toggle-folder-as-open set-active-board]]
            [story-planner.services.state.actions.projects :as projects]
            [story-planner.services.state.actions.canvas :refer [set-canvas-render]]
            [story-planner.services.state.actions.linking :refer [handle-linking]]))

; As we need more mutations for state we can add them here - Handle state change
; calls the correct method based on the type passed in
(defmulti handle-state-change (fn [action] (:type action)))
  (defmethod handle-state-change "set-login-error"
    [action]
    (swap! app-state conj {:loginError (:value action)}))
  (defmethod handle-state-change "set-canvas-render"
    [action]
    (set-canvas-render app-state (:value action)))
  (defmethod handle-state-change "get-projects"
    [action]
    (projects/update-projects app-state (:value action)))
  (defmethod handle-state-change "new-project"
    [action]
    (projects/add-new-project app-state (:value action)))
  (defmethod handle-state-change "get-project"
    [action]
    (projects/update-project app-state (:value action)))
  (defmethod handle-state-change "update-state-text"
    [action]
    (update-state-text app-state (:value action)))
  (defmethod handle-state-change "toggle-folder-open"
    [action]
    (toggle-folder-as-open app-state (:value action)))
  (defmethod handle-state-change "set-active-board"
    [action]
    (set-active-board app-state (:value action)))
  (defmethod handle-state-change "handle-linking-id"
    [action]
    (handle-linking app-state (:value action)))
