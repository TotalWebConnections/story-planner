(ns story-planner.components.folder-settings
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]))

(defn handle-close [showFolderSettings]
  (reset! showFolderSettings false)
  (set! (.-value (.getElementById js/document "folder-edit-value")) ""))

(defn handle-delete [showFolderSettings]
  (api/delete-folder {:folderId (:folderId @showFolderSettings)})
  (handle-close showFolderSettings))

(defn confirm-folder-delete [showFolderSettings]
  (let [confirmed? (js/confirm "Delete Folder?")]
    (if confirmed?
      (handle-delete showFolderSettings)
      nil)))

(defn handle-edit [showFolderSettings]
  (api/edit-folder
    {:folderId (:folderId @showFolderSettings)
     :name (.-value (.getElementById js/document "folder-edit-value"))})
  (handle-close  showFolderSettings))

(defn Folder-Settings [showFolderSettings]
  ; use the same css classes
  [:div.BoardSettings {:class (if @showFolderSettings "BoardSettings--active" nil)}
   [:div.BoardSettings__inner
    [:p.BoardSettings__inner__close.closeButton {:on-click #(handle-close showFolderSettings)} "x"]
    [:h2 "Folder Settings"]
    [:h3 "Edit Folder Name"]
    [:input {:type "text" :id "folder-edit-value" :placeholder (:name @showFolderSettings)}]
    [:button.--sideInput {:on-click #(handle-edit showFolderSettings)} "Save"]
    [:button.danger {:on-click #(confirm-folder-delete showFolderSettings)} "Delete Folder"]]])
