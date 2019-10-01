(ns story-planner.services.state.actions.linking)


(defn set-linking-id [state value]
  (swap! state conj {:linkStartId value}))