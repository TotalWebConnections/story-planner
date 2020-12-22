(ns story-planner.components.Overlay
  (:require [reagent.core :as reagent :refer [atom]]))

(defn get-input-value [id]
  "Grabs the input from the value - probably better to use an atom but this
   Should be fine for this simple case"
  (.-value (.getElementById js/document (str "Overlay__input-" id))))

(defn Overlay [active headerText onSubmit id] ; Takes an ID to make it unique to reuse
  [:div.Overlay {:class (str "Overlay--" @active)}
    [:div.Overlay__inner
      [:p.Overlay__inner__close {:on-click #(reset! active false)} "x"]
      [:h3.Overlay__inner-header headerText]
      [:input {:type "text" :placeholder "Folder Name" :id (str "Overlay__input-" id)}]
      [:button {:on-click #(onSubmit (get-input-value id))} "Save"]]])
