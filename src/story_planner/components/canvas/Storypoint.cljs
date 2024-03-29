(ns story-planner.components.canvas.Storypoint
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.services.scripts.components.storypoints :as storypointHelpers]
            [story-planner.services.scripts.components.entities :as entityHelpers]
            [story-planner.components.canvas.Linker :refer [Linker]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.canvas.Editable :refer [content-editable]]
            [story-planner.components.canvas.Editor :refer [Editor]]
            [story-planner.services.scripts.debounce :refer [debounce]]))


(defn update-storypoint-title [id value name-changed?]
  "Updates the value of storypoints title"
  (api/update-storypoint-title {:id id :value value})
  (js/setTimeout #(reset! name-changed? false) 500))

(def update-storypoint-title-debounced!
  (debounce update-storypoint-title 1000))

(defn update-storypoint-description [id value]
  "updates the value of a storypoints description"
  (api/update-storypoint-description {:id id :value value}))

(def update-storypoint-description-debounced!
  (debounce update-storypoint-description 1000))

(defn delete-storypoint [id]
  (api/delete-storypoint {:storypointId id}))

(defn initilize-link [id]
  (let [currentLink (get-from-state "linkStartId")]
    (if currentLink
      (if (= currentLink id)
        (handle-state-change {:type "handle-linking-id" :value nil})
        (do
          (handle-state-change {:type "handle-linking-id" :value nil})
          (api/add-link-to-storypoint {:storypointId currentLink :value id})))
      (handle-state-change {:type "handle-linking-id" :value id}))))

(defn update-link-label [storypointId linkId label]
  (api/update-link-label {:storypointId storypointId :linkId linkId :label label}))

(defn delete-link [storypointId linkId]
  (api/delete-link {:storypointId storypointId :linkId linkId}))

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
  (or (= linkId is-active) (and (not= nil label) (not= label ""))))

(defn get-label-position [starting-direction x-initial y-initial p2x p2y p3x p3y end-x end-y]
  {
   :top  (storypointHelpers/calcuate-point-on-curve 0.5 y-initial p2y p3y end-y)
   :left (- (storypointHelpers/calcuate-point-on-curve 0.5 x-initial p2x p3x end-x) 35)}) ; remove half width

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
           p3x (storypointHelpers/caculate-second-control-point-x starting-direction (- end-x x-initial) x-initial end-x)
           p3y (storypointHelpers/caculate-second-control-point-y starting-direction (- end-y y-initial) end-y)]
      [:div.Storypoint__curve {:key (str linkId (rand-int 100))}
       [:svg {:on-click #(on-curve-click is-active linkId) :pointer-events "all"
              :height "1px" :width "1px" :overflow "visible" :key  (str linkEndId "-" (rand-int 100))} ;1px prevents clicks and overflow dispalys whole thing
         [:defs
           [:marker {:id "head"
                     :orient "auto"
                     :viewBox "0 0 10 10"
                     :markerWidth "15"
                     :markerHeight "15"
                     :stroke "white"
                     :refX "1"
                     :refY "2"}
             [:path {:d "M0,0 V4 L2,2 Z" :fill "white"}]]]


         [:path {:fill "transparent" :stroke (if (= linkId @is-active) "red" "white") :stroke-width "2"
                 :d (str "M"x-initial","y-initial"
                      C"p2x","p2y"
                     "p3x","p3y"
                      "end-x","end-y"")
                  :marker-end "url(#head)"}]
         ; this second path is hidden, but still has click events so we can make it wider to make clicking easier
         [:path {:fill "transparent" :stroke "transparent" :stroke-width "10"
                 :d (str "M"x-initial","y-initial"
                      C"p2x","p2y"
                     "p3x","p3y"
                      "end-x","end-y"")}]]
       (if (show-curve-label @is-active linkLabel linkId)
         [:div.Storypoint__curve__label {:style (get-label-position starting-direction x-initial y-initial p2x p2y p3x p3y end-x end-y)} ; TODO we may want to make this closer
          [:input {:type "text" :style {:width (str (* 9 (count linkLabel)) "px") :min-width (if (= linkId @is-active) "155px" "50px")} :default-value linkLabel :placeholder "label" :id (str "linkLabelId-" linkId) :on-click #(reset! is-active linkId)}]
          (if (= linkId @is-active)
            [:div
              [:button {:on-click #(do (reset! is-active false)(update-link-label storypointId linkId (.-value (.getElementById js/document (str "linkLabelId-" linkId)))))} "Save"]
              [:button.danger {:on-click #(do (reset! is-active false)(delete-link storypointId linkId))} "Delete"]])])]))))

(defn on-add-image [id]
  (handle-state-change {:type "app-show-media-manager" :value "active"})
  (handle-state-change {:type "set-edited-storypoint" :value id}))

(defn get-count-after [linker content]
  (let [first-pos (:position @linker)
        current-count (count (clojure.string/replace content #"&nbsp;" ""))]
    (if (< current-count first-pos)
      (reset! linker {:active false :position nil :current-distance 0})
      (swap! linker conj {:current-distance (- current-count first-pos) :text (subs content first-pos (+ current-count first-pos))}))))

(defn strip-tags
  "pulls out p tags for checking lengths"
  [content]
  (clojure.string/replace (clojure.string/replace content #"<p>" "") #"</p>" ""))

(defn handler-linker-logic [linker content]
  (let [stripped-content (strip-tags content)]
    (if (:active @linker)
      (get-count-after linker stripped-content)
      (if (= (last stripped-content) "@")
        (swap! linker conj {:active true :position (count stripped-content)})
        nil))))

(defn add-linked-entity [linker storypoint entity]
  ;TODO this will only work if the @ is as the end
  (let [stripped-desc (subs (strip-tags (:description storypoint)) 0 (- (:position @linker) 1))]
    (update-storypoint-description (:id storypoint) (str stripped-desc "<a href=\"#"(:id entity)"\">"(:title entity)"</a> "))
    (reset! linker {:active false :position nil :current-distance 0})))

(defn click-on-linked-text [e]
  (let [id (second (clojure.string/split (-> e .-target .-href) #"#"))]
    (if id
      (handle-state-change {:type "set-entity-overlay-active" :value (entityHelpers/get-entity-by-id id)})
      nil)))

(defn on-editor-change [linker input-values storypoint value]
  (update-storypoint-description-debounced! (:id storypoint) value)
  (handler-linker-logic linker value))

(defn Storypoint [storypoint]
  (let [input-values (atom {:name (:name storypoint) :description (:description storypoint)})
        name-changed? (atom false)
        is-active (atom false)
        dropdown-active (atom false)
        linker (atom {:active false :position nil :current-distance 0})]
    (fn [storypoint]
      ; fires when an external event has updated a storypoint title
      (when-not @name-changed?
        (if-not (= (:name storypoint) (:name @input-values)) (swap! input-values conj {:name (:name storypoint)}) nil))
      (let [entity (if (:entityId storypoint) (entityHelpers/get-entity-by-id (:entityId storypoint)) nil)
            image (or (:image entity) (:image storypoint))]
        [:div.Storypoint.draggable {:key (:id storypoint) :id (:id storypoint) :class (if (= (get-from-state "linkStartId") (:id storypoint)) "Storypoint-currentlyLinked")
                                    :data-x (:x (:position storypoint))
                                    :data-y (:y (:position storypoint))
                                    :style {
                                            :transform (str "translate("(:x (:position storypoint))"px,"(:y (:position storypoint))"px)")
                                            :height (:h (:size storypoint)) :width (:w (:size storypoint))}}
          [:div.Storypoint__resizer.resize-topleft]
          [:div.Storypoint__resizer.resize-topright]
          [:div.Storypoint__resizer.resize-bottomright]
          [:div.Storypoint__resizer.resize-bottomleft]
          (if (:active storypoint)
            (do
              [:div.Storypoint__dragHandle.Storypoint__dragHandle-topRight]
              [:div.Storypoint__dragHandle.Storypoint__dragHandle-topLeft]
              [:div.Storypoint__dragHandle.Storypoint__dragHandle-bottomRight]
              [:div.Storypoint__dragHandle.Storypoint__dragHandle-bottomLeft]))
          (doall (for [link (:links storypoint)]
                  (draw-curve (:position storypoint) (:size storypoint) (:id link) (:linkId link) (:label link) is-active (:id storypoint))))
          [:div.Storypoint__inner
           [:div.Storypoint__header
            [:input
              {:type "text"
               :disabled (if entity true false)
               :value (if entity (:title entity) (:name @input-values))
               :on-click #(reset! dropdown-active false)
               :on-change #(do (reset! name-changed? true) (swap! input-values conj {:name (-> % .-target .-value)}) (update-storypoint-title-debounced! (:id storypoint) (-> % .-target .-value) name-changed?))}]
            [:div.Storypoint__header-right
             (if (= (get-from-state "linkStartId") (:id storypoint))
               [:i.fas.fa-unlink {:on-click #(initilize-link (:id storypoint))}]
               [:i.fas.fa-link {:on-click #(initilize-link (:id storypoint))}])
             [:i.Storypoint__header__options.fas.fa-ellipsis-v {:on-click #(reset! dropdown-active (if @dropdown-active false "active"))}]
             [:div.Storypoint__header__optionsDropDown {:class @dropdown-active}
               [:p {:on-click #(delete-storypoint (:id storypoint))} "Delete"]
               (if (not entity)
                (if image
                  [:p {:on-click #(api/update-storypoint-image {:id (:id storypoint) :value nil})} "Delete Image"]
                  [:p {:on-click #(on-add-image (:id storypoint))} "Add Image"])
                nil)]]]
           (if image
             [:div.Storypoint__image
               [:img {:src (str "https://story-planner.s3.amazonaws.com/" image) :width "100%"}]])
           [:div.textArea {:on-click #(click-on-linked-text %)}
            [Editor (:description storypoint) (partial on-editor-change linker input-values storypoint)]]
           [Linker @linker (:h (:size storypoint)) (partial add-linked-entity linker storypoint)]]]))))
