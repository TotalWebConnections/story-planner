(ns story-planner.services.state.actions.entities)


(defn update-entities [state value]
  (swap! state conj {:entities value}))