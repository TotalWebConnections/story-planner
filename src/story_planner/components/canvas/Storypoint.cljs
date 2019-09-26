(ns story-planner.components.canvas.Storypoint
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]))

(defn update-storypoint-title [id value]
  "Updates the value of storypoints title"
  (api/update-storypoint-title {:id id :value value}))

(defn update-storypoint-description [id value]
  "updates the value of a storypoints description"
  (api/update-storypoint-description {:id id :value value}))


(defn Storypoint [storypoint]
  [:div.Storypoint.draggable {:key (:id storypoint) :id (:id storypoint)
                        :data-x (:x (:position storypoint))
                        :data-y (:y (:position storypoint))
                        :style {:transform (str "translate("(:x (:position storypoint))"px,"(:y (:position storypoint))"px)")}}
    [:input
      {:type "text"
       :default-value (:name storypoint)
       :on-change #(update-storypoint-title (:id storypoint) (-> % .-target .-value))}]
    [:textarea {:default-value (:description storypoint)
                :on-change #(update-storypoint-description (:id storypoint) (-> % .-target .-value))}]])