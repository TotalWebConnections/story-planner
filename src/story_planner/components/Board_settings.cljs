(ns story-planner.components.Board-settings
  (:require [story-planner.services.scripts.api.api :as api]))

(defn handle-close [showBoardSettings]
  (reset! showBoardSettings false)
  (set! (.-value (.getElementById js/document "board-edit-name-value")) ""))

(defn handle-delete [showBoardSettings]
  (api/delete-board {:id (:id @showBoardSettings)})
  (handle-close showBoardSettings))

(defn confirm-board-delete [showBoardSettings]
  (let [confirmed? (js/confirm "Delete Board and All Associated Storypoints? This cannot be undone.")]
    (if confirmed?
      (handle-delete showBoardSettings)
      nil)))

(defn handle-edit [showBoardSettings]
  (api/edit-board-name
    {:id (:id @showBoardSettings)
     :name (.-value (.getElementById js/document "board-edit-name-value"))})
  (handle-close showBoardSettings))

(defn Board-Settings [showBoardSettings]
  [:div.BoardSettings {:class (if @showBoardSettings "BoardSettings--active" nil)}
   [:div.BoardSettings__inner
    [:p.BoardSettings__inner__close.closeButton {:on-click #(handle-close showBoardSettings)} "x"]
    [:h2 "Board Settings"]
    [:p.BoardSettings__flavorText "We're hard at work adding more board settings here such as background images and
         other features"]
    [:h3 "Edit Board Name"]
    [:input {:type "text" :id "board-edit-name-value" :placeholder (:name @showBoardSettings)}]
    [:button.--sideInput {:on-click #(handle-edit showBoardSettings)} "Save"]
    [:button.danger {:on-click #(confirm-board-delete showBoardSettings)} "Delete Board"]]])