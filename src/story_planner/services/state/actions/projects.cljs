(ns story-planner.services.state.actions.projects)

(defn update-projects [state value ]
  "Adds list of projects to the state"
    (swap! state conj {:projects value}))