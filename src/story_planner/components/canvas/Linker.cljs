(ns story-planner.components.canvas.Linker
  (:require [story-planner.services.state.global :refer [get-from-state]]))

(def MIN_FILTER 2)

(defn Linker [linker top-pos on-click]
  (let [entities (:entities (get-from-state "currentProject"))]
    [:div.Linker {:style {:top top-pos} :class (if (:active linker) "active")}
      (if (> (:current-distance linker) MIN_FILTER)
        [:ul.Linker__list
         (for [entity entities]
           [:li {:key (:title entity) :on-click #(on-click entity)} (:title entity)])]
        [:p "Start Typing To Filter Entities"])]))
