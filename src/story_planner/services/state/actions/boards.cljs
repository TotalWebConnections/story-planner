(ns story-planner.services.state.actions.boards)



(defn new-board [state value]
  "adds a folder to state based on type"
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :boards] conj (:value value))))
