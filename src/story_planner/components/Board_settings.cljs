(ns story-planner.components.Board-settings
  (:require [story-planner.services.scripts.api.api :as api]))

(defn handle-delete [showBoardSettings]
  (api/delete-board {:id @showBoardSettings})
  (reset! showBoardSettings false))

(defn confirm-board-delete [showBoardSettings]
  (let [confirmed? (js/confirm "Delete Board and All Associated Storypoints? This cannot be undone.")]
    (if confirmed?
      (handle-delete showBoardSettings)
      nil)))

(defn Board-Settings [showBoardSettings]
  [:div.BoardSettings {:class (if @showBoardSettings "BoardSettings--active" nil)}
   [:div.BoardSettings__inner
    [:p.BoardSettings__inner__close.closeButton {:on-click #(reset! showBoardSettings false)} "x"]
    [:h2 "Board Settings"]
    [:p "We're hard at work adding more board settings here such as background images and
         other features"]
    [:button.danger {:on-click #(confirm-board-delete showBoardSettings)} "Delete Board"]]])