(ns story-planner.services.state.actions.storypoints)

(defn set-new-position [old new]
  (conj old {:position (:position new) :size (:size new)}))

(defn handle-position-change [storypoints value]
  (map #(if (= (:id %) (:storypointId value)) (set-new-position % value) %)) storypoints)

(defn handle-title-change [storypoints value]
  (map #(if (= (:id %) (:storypointId value)) (conj % {:name (:value value)}) %) storypoints))

(defn handle-description-change [storypoints value]
  (map #(if (= (:id %) (:storypointId value)) (conj % {:description (:value value)}) %) storypoints))

(defn add-storypoint [state value]
  (print value)
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :storypoints] conj value)
    nil))

(defn update-storypoint-position [state value]
  (swap! state update-in [:currentProject :storypoints] handle-position-change value))

(defn update-storypoint-title [state value]
  (swap! state update-in [:currentProject :storypoints] handle-title-change value))

(defn update-storypoint-description [state value]
  (swap! state update-in [:currentProject :storypoints] handle-description-change value))
