(ns story-planner.components.media.folder
  (:require [story-planner.services.scripts.api.upload :refer [create-media-folder]]))

(defn Folder-creation []
  (let [folder-name (atom "")]
    (fn []
      [:div.Folder-creation
       [:input {:type "text" :placeholder "Folder Name" :on-change #(reset! folder-name (-> % .-target .-value))}]
       [:button  {:on-click #(create-media-folder folder-name)} "Submit"]])))
