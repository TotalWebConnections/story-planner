(ns story-planner.services.state.actions.projects)

(defn update-projects [state value]
  "Adds list of projects to the state"
    (swap! state conj {:projects value}))

(defn add-new-project [state value]
  (swap! state update-in [:projects] conj (first value)))

(defn delete-project [state value]
  (swap! state update-in [:projects]
    (fn [projects]
      (filter #(not (= value (:_id %))) projects))))


(defn update-project [state value]
  "updates the current project with the new state"
    ; We can safely take first here here as the vector will
    ; only be an elemnt of size 1
    (swap! state conj {:currentProject (first value)}))

(defn update-images [state value]
  "Updates our images list"
  (print value)
  (swap! state conj {:images (:images value)})
  (swap! state conj {:media-folders (:folders value)}))

(defn add-image [state value]
  (swap! state update-in [:images] conj value))
