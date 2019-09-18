(ns story-planner.services.state.actions.folders)



(defn update-folders [state value type]
  "adds a folder to state based on type"
  (if (= "board" type)
    (swap! state conj {:boardFolders value})
    (swap! state conj {:entityFolders value})))


(defn toggle-folder-as-open [state folderName]
  (swap! state update-in [:currentProject :folders] (fn [item]
    (map (fn [folder]
      (if (= folderName (:name folder))
        (conj folder {:active (not (:active folder))})
         folder)) item))))


(defn set-active-board [state board]
  (swap! state conj {:currentBoard board}))