(ns story-planner.services.state.actions.linking)

(defn handle-linking [state value]
  "Determines whether to start a link or complete it"
  "if link exitsts we finish link, otherwise it's a fresh link"
  (swap! state conj {:linkStartId value}))