(ns story-planner.services.state.actions.projects)

(defn update-projects [state value ]
  "Adds list of projects to the state"
    (swap! state conj {:projects value}))

(defn update-project [state value ]
  "updates the current project with the new state"
    ; We can safely take first here here as the vector will
    ; only be an elemnt of size 1
    (swap! state conj {:currentProject (first value)}))