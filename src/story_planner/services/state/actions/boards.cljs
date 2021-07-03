(ns story-planner.services.state.actions.boards)

(defn new-board [state value]
  "adds a folder to state based on type"
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :boards] conj (:value value))))

(defn delete-board-handler [boards value]
  (remove #(= (:id %) value) boards))

(defn delete-board [state value]
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :boards] delete-board-handler (:id value))
    nil))

(defn edit-board-name-handler [boards value]
  (map #(if (= (:id %) (:id value)) (conj % {:name (:name value)}) %) boards))

(defn edit-board-name [state value]
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :boards] edit-board-name-handler value)
    nil))
