(ns story-planner.components.canvas.Linker
  (:require [story-planner.services.state.global :refer [get-from-state]]))


(defn Linker [active top-pos]
  (let [entities (:entities (get-from-state "currentProject"))]
    [:div.Linker {:style {:top top-pos} :class (if active "active")}
      [:ul.Linker__list
       (for [entity entities]
         [:li {:on-click #(print (:title entity))} (:title entity)])]]))

