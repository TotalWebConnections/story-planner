(ns story-planner.services.state.actions.folders)



(defn update-folders [state value type]
  "adds a folder to state based on type"
  (if (= "board" type)
    (swap! state update-in [:currentProject :folders] conj value)
    (swap! state update-in [:currentProject :folders] conj value)))

(defn delete-folder-handler [folders value]
  (remove #(= (:folderId %) value) folders))

(defn delete-folder [state value]
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :folders] delete-folder-handler (:folderId value))
    nil))

(defn edit-folder-handler [folders value]
  (map #(if (= (:folderId %) (:folderId value)) (conj % {:name (:name value)}) %) folders))

(defn edit-folder [state value]
  (if (= (:projectId value) (-> @state :currentProject :_id))
    (swap! state update-in [:currentProject :folders] edit-folder-handler value)
    nil))
;
; (defn toggle-folder-as-open [state folderName]
;   (swap! state update-in [:currentProject :folders] (fn [item]
;     (map (fn [folder]
;       (if (= folderName (:name folder))
;         (conj folder {:active (not (:active folder))})
;          folder)) item))))


(defn toggle-folder-as-open [state folderValues]
  (swap! state conj {:openedFolders {(keyword (:name folderValues)) (:value folderValues)}}))


(defn set-active-board [state board]
  (swap! state conj {:currentBoard board}))