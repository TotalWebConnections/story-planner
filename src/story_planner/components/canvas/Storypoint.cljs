(ns story-planner.components.canvas.Storypoint
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.services.scripts.components.storypoints :as storypointHelpers]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))

(defn update-storypoint-title [id value]
  "Updates the value of storypoints title"
  (api/update-storypoint-title {:id id :value value}))

(defn update-storypoint-description [id value]
  "updates the value of a storypoints description"
  (api/update-storypoint-description {:id id :value value}))

(defn initilize-link [id]
  (let [currentLink (get-from-state "linkStartId")]
    (if currentLink
      (if (= currentLink id)
        (js/alert "Can't Link To Same Point!")
        (do
          (handle-state-change {:type "handle-linking-id" :value nil})
          (api/add-link-to-storypoint {:storypointId currentLink :value id})))
      (handle-state-change {:type "handle-linking-id" :value id}))))

; TODO need to account for different diections
(defn draw-curve [position size linkEndId]
  (let [currentPoint (storypointHelpers/get-storypoint-by-id (:storypoints (get-from-state "currentProject")) linkEndId)
        end-x (- (:x (:position currentPoint)) (:x position)) ; Should be x pos of end - the x offset of the original since 0,0 is relative to the first elem
        end-y (- (+ (* 0.5 (:h (:size currentPoint))) (:y (:position currentPoint))) (:y position))] ; y pos + 1/2 height - y offset of firs - make it positive
    (if currentPoint
     (let [x-initial (storypointHelpers/calculate-curve-x-initial size) ; Should just be the width
           y-initial (storypointHelpers/calculate-curve-y-initial size)] ; should be half the height
      [:svg {:height "auto" :width "auto" :overflow "visible"}
        [:path {:fill "transparent" :stroke "white" :stroke-width "2"
                :d (str "M"x-initial","y-initial"
                     C"(+ x-initial (/ (- end-x x-initial) 3))","(- y-initial 50)"
                    "(+ x-initial x-initial (/ (- end-x x-initial) 3))","(+ 50 end-y)"
                     "end-x","end-y"")} ]]
))))

(defn Storypoint [storypoint]
  [:div.Storypoint.draggable {:key (:id storypoint) :id (:id storypoint)
                        :data-x (:x (:position storypoint))
                        :data-y (:y (:position storypoint))
                        :style {:transform (str "translate("(:x (:position storypoint))"px,"(:y (:position storypoint))"px)")
                                :height (:h (:size storypoint)) :width (:w (:size storypoint))}}
    (draw-curve (:position storypoint) (:size storypoint) (:id (first (:links storypoint))))
    [:p {:on-click #(initilize-link (:id storypoint))} "link"]
    [:input
      {:type "text"
       :default-value (:name storypoint)
       :on-change #(update-storypoint-title (:id storypoint) (-> % .-target .-value))}]
    [:textarea {:default-value (:description storypoint)
                :on-change #(update-storypoint-description (:id storypoint) (-> % .-target .-value))}]])