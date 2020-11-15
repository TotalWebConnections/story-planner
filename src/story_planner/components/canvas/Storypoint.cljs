(ns story-planner.components.canvas.Storypoint
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.services.scripts.components.storypoints :as storypointHelpers]
            [story-planner.services.scripts.components.entities :as entityHelpers]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]))

(defn update-storypoint-title [id value]
  "Updates the value of storypoints title"
  (api/update-storypoint-title {:id id :value value}))

(defn update-storypoint-description [id value]
  "updates the value of a storypoints description"
  (api/update-storypoint-description {:id id :value value}))

(defn delete-storypoint [id]
  (api/delete-storypoint {:storypointId id}))

(defn initilize-link [id]
  (let [currentLink (get-from-state "linkStartId")]
    (if currentLink
      (if (= currentLink id)
        (js/alert "Can't Link To Same Point!")
        (do
          (handle-state-change {:type "handle-linking-id" :value nil})
          (api/add-link-to-storypoint {:storypointId currentLink :value id})))
      (handle-state-change {:type "handle-linking-id" :value id}))))

(defn update-link-label [storypointId linkId label]
  (api/update-link-label {:storypointId storypointId :linkId linkId :label label}))

(defn get-direction-for-top [y]
  (if (> y 0)
    "Top"
    "Bottom"))
(defn get-direction-for-side [x]
  (if (> x 0)
   "Left"
   "Right"))

(defn get-relative-position [point1 point2]
  (let [x (- (:x point1) (:x point2))
        y(- (:y point1) (:y point2))]
   (if (> (.abs js/Math x) (.abs js/Math y))
     (get-direction-for-side x)
     (get-direction-for-top y))))

(defn show-curve-label [is-active label linkId]
  (or (= linkId is-active) (not= nil label)))

(defn get-label-position [starting-direction x-initial y-initial p2x p2y p3x]
  (cond
    (= starting-direction "Top") {:top p2y :left p2x}
    (= starting-direction "Bottom") {:top p2y :left x-initial}
    (= starting-direction "Left") {:top y-initial :left p3x}
    (= starting-direction "Right") {:top p2y :left p2x}))

(defn on-curve-click [is-active id]
  (if @is-active
    (reset! is-active false)
    (reset! is-active id)))

(defn draw-curve [position size linkEndId linkId linkLabel is-active storypointId]
  (let [currentPoint (storypointHelpers/get-storypoint-by-id (:storypoints (get-from-state "currentProject")) linkEndId)
        starting-direction (get-relative-position position (:position currentPoint))] ; y pos + 1/2 height - y offset of firs - make it positive
    (if currentPoint
     (let [x-initial (storypointHelpers/calculate-curve-x-initial size starting-direction) ; Should just be the width
           y-initial (storypointHelpers/calculate-curve-y-initial size starting-direction)
           end-x (storypointHelpers/calculate-curve-x-end (:size currentPoint) (:position currentPoint) position starting-direction) ; Should be x pos of end - the x offset of the original since 0,0 is relative to the first elem
           end-y (storypointHelpers/calculate-curve-y-end (:size currentPoint) (:position currentPoint) position starting-direction)
           p2x (storypointHelpers/caculate-first-control-point-x starting-direction (- end-x x-initial) x-initial)
           p2y (storypointHelpers/caculate-first-control-point-y starting-direction (- end-y y-initial) y-initial)
           p3x (storypointHelpers/caculate-second-control-point-x starting-direction (- end-x x-initial) x-initial)
           p3y (storypointHelpers/caculate-second-control-point-y starting-direction (- end-y y-initial) y-initial)]
      [:div.Storypoint__curve
       [:svg {:on-click #(on-curve-click is-active linkId)
              :height "1px" :width "1px" :overflow "visible" :key  (str linkEndId "-" (rand-int 100))} ;1px prevents clicks and overflow dispalys whole thing
         [:defs
           [:marker {:id "head"
                     :orient "auto"
                     :markerWidth "4"
                     :markerHeight "6"
                     :fill "red"
                     :stroke "white"
                     :refX "1"
                     :refY "2"}
             [:path {:d "M0,0 V4 L2,2 Z" :fill "white"}]]]


         [:path {:fill "transparent" :stroke (if (= linkId @is-active) "red" "white") :stroke-width "2"
                 :d (str "M"x-initial","y-initial"
                      C"p2x","p2y"
                     "p3x","p3y"
                      "(- end-x 4)","(- end-y 4)"")
                  :marker-end "url(#head)"}]]
       (if (show-curve-label @is-active linkLabel linkId)
         [:div.Storypoint__curve__label {:style (get-label-position starting-direction x-initial y-initial p2x p2y p3x)} ; TODO we may want to make this closer
          [:input {:type "text" :default-value linkLabel :placeholder "label" :id (str "linkLabelId-" linkId) :on-click #(reset! is-active linkId)}]
          (if (= linkId @is-active)
            [:button {:on-click #(do (reset! is-active false)(update-link-label storypointId linkId (.-value (.getElementById js/document (str "linkLabelId-" linkId)))))} "Save"])])]))))


(defn Storypoint [storypoint]
  (let [input-values (atom {:name (:name storypoint) :description (:description storypoint)})
        is-active (atom false)
        entity (if (:entityId storypoint) (entityHelpers/get-entity-by-id (:entityId storypoint)) nil)
        dropdown-active (atom false)]
    (fn [storypoint]
      [:div.Storypoint.draggable {:key (:id storypoint) :id (:id storypoint) :class (if (= (get-from-state "linkStartId") (:id storypoint)) "Storypoint-currentlyLinked")
                                  :data-x (:x (:position storypoint))
                                  :data-y (:y (:position storypoint))
                                  :style {
                                          :transform (str "translate("(:x (:position storypoint))"px,"(:y (:position storypoint))"px)")
                                          :height (:h (:size storypoint)) :width (:w (:size storypoint))}}
        (if (:active storypoint)
          (do
            [:div.Storypoint__dragHandle.Storypoint__dragHandle-topRight]
            [:div.Storypoint__dragHandle.Storypoint__dragHandle-topLeft]
            [:div.Storypoint__dragHandle.Storypoint__dragHandle-bottomRight]
            [:div.Storypoint__dragHandle.Storypoint__dragHandle-bottomLeft]))
        (doall (for [link (:links storypoint)]
                (draw-curve (:position storypoint) (:size storypoint) (:id link) (:linkId link) (:label link) is-active (:id storypoint))))
        [:div.Storypoint__header
         [:input
           {:type "text"
            :disabled (if entity true false)
            :default-value (if entity (:title entity) (:name @input-values))
            :on-click #(reset! dropdown-active false)
            :on-change #(do (swap! input-values conj {:name (-> % .-target .-value)})(update-storypoint-title (:id storypoint) (-> % .-target .-value)))}]
         [:div.Storypoint__header-right
          [:i.fas.fa-link {:on-click #(initilize-link (:id storypoint)) :style {:width "50px"}}]
          [:i.Storypoint__header__options.fas.fa-ellipsis-v {:on-click #(reset! dropdown-active (if @dropdown-active false "active"))}]
          [:div.Storypoint__header__optionsDropDown {:class @dropdown-active}
            [:p {:on-click #(delete-storypoint (:id storypoint))} "Delete"]
            [:p "Add Image"]]]]
        [:textarea {:default-value (:description storypoint)
                    :on-click #(reset! dropdown-active false)
                    :on-change #(do (swap! input-values conj {:description (-> % .-target .-value)})(update-storypoint-description (:id storypoint) (-> % .-target .-value)))}]])))
