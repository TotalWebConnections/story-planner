(ns story-planner.services.state.dispatcher
  (:require [story-planner.services.state.global :refer [app-state]]
            [story-planner.services.state.textstate :refer [update-state-text]]
            [story-planner.services.state.actions.folders :as folders]
            [story-planner.services.state.actions.projects :as projects]
            [story-planner.services.state.actions.canvas :refer [set-canvas-render set-show-media]]
            [story-planner.services.state.actions.linking :refer [handle-linking]]))

; As we need more mutations for state we can add them here - Handle state change
; calls the correct method based on the type passed in
(defmulti handle-state-change (fn [action] (:type action)))
  (defmethod handle-state-change "set-login-error"
    [action]
    (swap! app-state conj {:loginError (:value action)}))
  (defmethod handle-state-change "toggle-sidebar-active"
    [action]
    (swap! app-state conj {:sidebarActive (not (:sidebarActive @app-state))}))
  (defmethod handle-state-change "set-user"
    [action]
    (swap! app-state conj {:user (:value action)}))
  (defmethod handle-state-change "set-canvas-render"
    [action]
    (set-canvas-render app-state (:value action)))
  (defmethod handle-state-change "get-projects"
    [action]
    (projects/update-projects app-state (:value action)))
  (defmethod handle-state-change "get-authorized-users"
    [action]
    (swap! app-state conj {:users (:value action)}))
  (defmethod handle-state-change "get-images"
    [action]
    (projects/update-images app-state (:value action)))
  (defmethod handle-state-change "add-image"
    [action]
    (projects/add-image app-state (:value action)))
  (defmethod handle-state-change "remove-image"
    [action]
    (projects/remove-image app-state (:value action)))
  (defmethod handle-state-change "new-project"
    [action]
    (projects/add-new-project app-state (:value action)))
  (defmethod handle-state-change "delete-project"
    [action]
    (projects/delete-project app-state (:value action)))
  (defmethod handle-state-change "get-project"
    [action]
    (projects/update-project app-state (:value action)))
  (defmethod handle-state-change "update-state-text"
    [action]
    (update-state-text app-state (:value action)))
  (defmethod handle-state-change "toggle-folder-open"
    [action]
    (folders/toggle-folder-as-open app-state (:value action)))
  (defmethod handle-state-change "set-active-board"
    [action]
    (folders/set-active-board app-state (:value action)))
  (defmethod handle-state-change "handle-linking-id"
    [action]
    (handle-linking app-state (:value action)))


  ;Folder Functions
  (defmethod handle-state-change "new-folder"
    [action]
    (folders/update-folders app-state (:value action) (-> action :value :type)))

  ; Entity overlay pullout
  (defmethod handle-state-change "set-entity-overlay-active"
    [action]
    (swap! app-state update-in [:show-entitiy-overlay] conj {:show "active" :edit (:value action)}))
  (defmethod handle-state-change "set-entity-overlay-hidden"
    [action]
    (swap! app-state update-in [:show-entitiy-overlay] conj {:show false :edit false}))

  ; Image related stuff
  (defmethod handle-state-change "add-media-folder"
    [action]
    (swap! app-state update-in [:media-folders] conj (:value action)))

  ; IMAGE stuff for the stroypoints
  (defmethod handle-state-change "set-edited-storypoint"
    [action]
    (swap! app-state conj {:edited-storypoint (:value action)}))
  (defmethod handle-state-change "app-show-media-manager"
    [action]
    (set-show-media app-state (:value action)))

  ; D&D stuff
  (defmethod handle-state-change "set-drag-id"
    [action]
    (swap! app-state conj {:dragId (:value action)}))
  (defmethod handle-state-change "remove-drag-id"
    [action]
    (swap! app-state conj {:dragId nil}))


  ;Stuff related to authorized Users
  (defmethod handle-state-change "update-project-authorized-users"
    [action]
    (swap! app-state update-in [:projects]
      (fn [projects]
        (map
          #(if (= (:projectId (:value action)) (:_id %))
            (conj % {:authorizedUsers (:authorizedUsers (:value action))})
            %
            ) projects))))
