(ns story-planner.services.state.actions.linking)

(defn handle-linking [state value]
  "Determines whether to start a link or complete it"
  "if link exitsts we finish link, otherwise it's a fresh link"
  (swap! state conj {:linkStartId value}))

(defn add-link [storypoints value]
  (map #(if (= (:id %) (:storypointId value)) (update-in % [:links] conj (dissoc value :storypointId)) %) storypoints))

(defn add-link-to-storypoint [state value]
  (swap! state update-in [:currentProject :storypoints] add-link value))


(defn set-label [links value]
  (map #(if (= (:linkId %) (:linkId value)) (conj % {:label (:label value)}) %) links))

(defn update-label [storypoints value]
  (map #(if (= (:id %) (:storypointId value)) (update-in % [:links] set-label value) %) storypoints))

(defn update-link-label [state value]
  (swap! state update-in [:currentProject :storypoints] update-label value))


(defn remove-link [links value]
  (remove #(= (:linkId %) (:linkId value)) links))

(defn find-storypoint [storypoints value]
  (map #(if (= (:id %) (:storypointId value)) (update-in % [:links] remove-link value) %) storypoints))

(defn delete-link [state value]
  (swap! state update-in [:currentProject :storypoints] find-storypoint value))

