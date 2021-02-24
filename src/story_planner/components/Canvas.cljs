(ns story-planner.components.Canvas
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.services.state.global :refer [get-from-state]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.canvas.Controls :refer [Controls]]
            [story-planner.components.canvas.Storypoint :refer [Storypoint]]
            [story-planner.services.scripts.canvas :refer [get-current-board-storypoints]]
            ; ["@panzoom/panzoom" :as panzoom]
            ["interactjs" :as interact]
            ["displacejs" :as displace]))


(set! js/global js/window); Work around as panzoom will error on global sometimes
(def panzoom (.-Panzoom js/window))
(def panHandler-ref (atom nil)) ; we use this so child-components can have access to the current state of the canvas
(def drag-ref (atom [])) ; reference to our draggable elements
(def elementSize (atom {}))

(defn getXVal [target event]
  "Grabs the x value for the onDrag event"
  (let [x (+ (js/parseFloat (.getAttribute target "data-x")) (.-dx event))]
    (if (or (js/isNaN x) (< x 0))
      0
      x)))

(defn getYVal [target event]
  "Grabs the y value for the onDrag event"
  (let [y (+ (js/parseFloat (.getAttribute target "data-y")) (.-dy event))]
    (if (or (js/isNaN y) (< y 0))
      0
      y)))


(defn update-storypoint-position [x y height width id]
  (api/update-storypoint-position {:x x
                                   :y y
                                   :height height
                                   :width width
                                   :id id}))
; THis is also in controls TODO move it to one place
(defn get-zoom-modifier [zoomLevel]
  (cond
    (> zoomLevel 1) zoomLevel
    (= zoomLevel 1) zoomLevel
    :else (- zoomLevel 0.025)))

(defn handle-add-storypoint [projectId board entityId]
  (let [currentPan (js->clj (.getPan @panHandler-ref) :keywordize-keys true)
        zoomLevel (get-zoom-modifier (.getScale @panHandler-ref))]
    (api/create-storypoint {:projectId projectId
                            :board board
                            :entityId entityId
                            :position {:x (* (* -1 (:x currentPan )) zoomLevel) :y (* (* -1 (:y currentPan)) zoomLevel)}
                            :size {:h 200 :w 300}})))


; We need to setup all our handlers after the componeent has rendered
; TODO test with state - think a rerender will break everything - maybe set a global `handlersSet` ?
; TODO this does break on a reload - probably need a flag to only do this once or some sort of cleanup?
(defn render-canvas []
  (def zoomElem (.querySelector js/document "#Canvas"))
  (if zoomElem
    (do
      (def panHandler (panzoom zoomElem (clj->js {:maxScale 1
                                                  :minScale 0.125
                                                  :step 0.05
                                                  :excludeClass "draggable"
                                                  :contain "outside"})))
      (reset! panHandler-ref panHandler)
      ;
      (js/setTimeout #(.pan panHandler -2500 -2500))
      (js/setTimeout #(.addEventListener zoomElem "wheel" (.-zoomWithWheel panHandler)))
  ; function taken from interact - probably not `functional`
  ; TODO we should chanage drag speed based on zoom level
  ; The further out the faster the zoom needs to be to seem fluid


     (defn onMoveHandler [event]
       (let [target (.-target event)]
         (let [x (getXVal target event)
               y (getYVal target event)]
           (set! (.-transform (.-style target)) (str "translate("x"px, "y"px)"))
           (set! (.-webkitTransform (.-style target)) (str "translate("x"px, "y"px)"))
           (.setAttribute target "data-x" x)
           (.setAttribute target "data-y" y))))


     (defn onMoveEndHandler [event]
       (let [target (.-target event)]
         (api/update-storypoint-position {:x (getXVal target event)
                                          :y (getYVal target event)
                                          :height (:height @elementSize)
                                          :width (:width @elementSize)
                                          :id (.getAttribute target "id")})))
     (defn onMoveStart [event]
       ;We set this here as the scale of the zoom can throw off size after a drag event
       ;we can use the zoom to get back the previous values, but it's often off by 1
       (let [target (.-target event)]
         (reset! elementSize {:height (.-offsetHeight target) :width (.-offsetWidth target)})))

     (defn onResize [event]
       (let [target (.-target event)
             x (js/parseFloat (.getAttribute target "data-x"))
             y (js/parseFloat (.getAttribute target "data-y"))
             newX (+ x (.-left (.-deltaRect event)))
             newY (+ y (.-top (.-deltaRect event)))
             scale (.getScale panHandler)] ; we need the scale as the resize will be wrong unless we account for the differnet zooms

       ; set the size
        (set! (.-height (.-style (.-target event))) (str (/ (.-height (.-rect event)) scale) "px"))
        (set! (.-width (.-style (.-target event))) (str (/ (.-width (.-rect event)) scale) "px"))

       ;translate when resizing from top or edges

        (.setAttribute target "data-x" newX)
        (.setAttribute target "data-y" newY)
        (set! (.-transform (.-style target)) (str "translate("newX"px, "newY"px)"))
        (set! (.-webkitTransform (.-style target)) (str "translate("newX"px, "newY"px)"))

        (update-storypoint-position newX newY (/ (.-height (.-rect event)) scale) (/ (.-width (.-rect event)) scale) (.getAttribute target "id"))))

     (.draggable (interact ".draggable") (clj->js {:inertia false :onmove onMoveHandler :onend onMoveEndHandler :onstart onMoveStart}))
     (.resizable (interact ".draggable") (clj->js {:edges {:left true :right true :bottom true :top true}
                                                   :listeners {:move onResize}})))))


(defn allow-drop [e]
  (.preventDefault e))

(defn handle-drop [e projectId board]
  (handle-add-storypoint projectId board (get-from-state "dragId"))
  (handle-state-change {:type "remove-drag-id" :value nil}))

(defn Canvas [currentProject currentBoard linkStartId]
  (reagent/create-class                 ;; <-- expects a map of functions
    {:display-name  "canvas"      ;; for more helpful warnings & errors

      :component-did-mount               ;; the name of a lifecycle function
        (fn [this]
          (render-canvas)
          (println "component-did-mount")) ;; your implementation

       :component-did-update              ;; the name of a lifecycle function
        (fn [this old-argv])                ;; reagent provides you the entire "argv", not just the "props"



        ;; other lifecycle funcs can go in here
        :reagent-render        ;; Note:  is not :render
        (fn [currentProject currentBoard linkStartId]           ;; remember to repeat parameters
          [:div.CanvasParent {:on-drop #(handle-drop % (:_id currentProject) currentBoard) :on-drag-over allow-drop}
            [Controls (:_id currentProject) currentBoard @panHandler-ref]
            [:div#Canvas
             (if currentBoard
               (for [storypoint (get-current-board-storypoints (:storypoints currentProject) currentBoard)] ; TODO actually pull the current
                 [:div {:key (:id storypoint)}
                   [Storypoint storypoint]]))]])}))

; TODO we can probably just work with the abpve canvas - remove this and import the component
(defn render [currentProject currentBoard linkStartId]
   [Canvas currentProject currentBoard linkStartId])

