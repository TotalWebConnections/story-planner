(ns story-planner.services.state.actions.entities)


(defn update-edited-entity [entities value]
  (map #(if (= (:id %) (:id value)) value %) entities))

(defn delete-entity-handler [entities value]
  (remove #(= (:id %) (:id value)) entities))

(defn add-entity [state value]
  ;This handles the case where users are in two differnt projects that they have access to
  ;TODO we should probably work on making it so when a switche projects it knows
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :entities] conj value)
    nil))

(defn edit-entity [state value]
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :entities] update-edited-entity value)
    nil))

(defn delete-entity [state value]
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :entities] delete-entity-handler value)
    nil))

