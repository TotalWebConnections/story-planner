(ns story-planner.services.scripts.folders)


; TODO unit tests around this 
(defn get-folders-by-type [folders]
  "Sorts folders by entity and board"
  (group-by :type folders))