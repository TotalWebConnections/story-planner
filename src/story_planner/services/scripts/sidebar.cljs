(ns story-planner.services.scripts.sidebar)


(defn generate-folder-structure []
  "generates the folder structure for the sidebar")


(defn get-boards-by-folders [folders boards]
  "Matches up all the boards with their associated folder"
  (map (fn [folder]
        (conj folder {:boards (filter (fn [board]
                                       (if (= (:folder board) (:name folder))
                                         true
                                         false)) boards)}))
    folders))
