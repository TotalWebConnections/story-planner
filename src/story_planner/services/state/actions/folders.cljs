(ns story-planner.services.state.actions.folders)



(defn update-folders [state value type]
  "adds a folder to state based on type"
  (if (= "board" type)
    (swap! state conj {:boardFolders value})
    (swap! state conj {:entityFolders value})))