(ns story-planner.components.canvas.Editor
  (:require [reagent.core :as reagent :refer [atom]]
            [cljsjs.quill]
            [story-planner.components.canvas.Quill :as Quill]
            [story-planner.services.scripts.debounce :refer [debounce]]))

(defn reset-edit-mode [in-edit?]
  (print "Edit Mode reset")
  (reset! in-edit? false))

;; note how we use def instead of defn
(def reset-edit-mode-debounced!
  (debounce reset-edit-mode 1000))


(defn handle-change [in-edit? on-change value]
  (on-change value)

  (if-not @in-edit?
    (reset! in-edit? true)
    (reset-edit-mode-debounced! in-edit?)))


(defn Editor [description on-change]
  (let [in-edit? (atom false)]
    (fn [description on-change]
      [:div.Editor {:style {:height "100%"}}
       [Quill/editor
        {:id "quill-editor"
         :content description
         :in-edit? @in-edit?
         :selection nil
         :on-change-fn #(if (= % "user")
                          (handle-change in-edit? on-change %2)
                          nil)}]])))
