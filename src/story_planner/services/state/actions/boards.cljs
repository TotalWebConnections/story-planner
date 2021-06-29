(ns story-planner.services.state.actions.boards)

(defn new-board [state value]
  "adds a folder to state based on type"
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :boards] conj (:value value))))

(defn delete-board-handler [boards value]
  (print boards)
  (print value)
  (remove #(= (:id %) value) boards))

(defn delete-board [state value]
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :boards] delete-board-handler (:id value))
    nil))
