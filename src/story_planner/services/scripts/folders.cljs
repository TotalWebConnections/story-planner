(ns story-planner.services.scripts.folders)


; TODO unit tests around this
(defn get-folders-by-type [folders]
  "Sorts folders by entity and board"
  (group-by :type folders))

(defn assign-entities-to-parent-folder [folders entities]
  (map (fn [folder]
         (conj folder {:entities (filter (fn [entity]
                                           (if (= (:folder entity) (:folderId folder)) true false)) entities)})) folders))
