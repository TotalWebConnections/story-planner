(ns story-planner.components.Canvas
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.canvas.Controls :refer [Controls]]
            [story-planner.components.canvas.Storypoint :refer [Storypoint]]
            [story-planner.services.scripts.canvas :refer [get-current-board-storypoints]]
            ["panzoom" :as panzoom]
            ["interactjs" :as interact]
            ["displacejs" :as displace]))

(set! js/global js/window); Work around as panzoom will error on global sometimes
(def drag-ref (atom [])) ; reference to our draggable elements

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


; We need to setup all our handlers after the componeent has rendered
; TODO test with state - think a rerender will break everything - maybe set a global `handlersSet` ?
; TODO this does break on a reload - probably need a flag to only do this once or some sort of cleanup?
(defn render-canvas []
  (def zoomElem (.querySelector js/document "#Canvas"))
  (if zoomElem
    (do
  (def panHandler (panzoom zoomElem (clj->js {:maxZoom 4 :minZoom 0.1
                                              :minScale 1
                                              :boundsPadding 1 ; it multiplies by this is in the code for panzoom
                                              :bounds true})))

  (.moveTo panHandler -2500 -2500)
  ; function taken from interact - probably not `functional`
  ; TODO we should chanage drag speed based on zoom level
  ; The further out the faster the zoom needs to be to seem fluid
  (defn onMoveHandler [event]
    (js/console.log event)
    (.pause panHandler) ; resume call on end on end
;     (let [target (.-target event)]
;       (let [x (getXVal target event)
;             y (getYVal target event)]
;         (set! (.-transform (.-style target)) (str "translate("x"px, "y"px)"))
;         (set! (.-webkitTransform (.-style target)) (str "translate("x"px, "y"px)"))
;         (.setAttribute target "data-x" x)
;         (.setAttribute target "data-y" y)))
  )

  (defn onMoveEndHandler [event]
    (js/console.log event)
    ; (let [target (.-target event)]
    ;   (api/update-storypoint-position {:x (getXVal target event)
    ;                                    :y (getYVal target event)
    ;                                    :id (.getAttribute target "id")}))
    (.resume panHandler))

  ; (.draggable (interact ".draggable") (clj->js {:inertia false :onmove onMoveHandler :onend onMoveEndHandler}))
  )
))

(defn Canvas [currentProject currentBoard linkStartId]
  (reagent/create-class                 ;; <-- expects a map of functions
    {:display-name  "canvas"      ;; for more helpful warnings & errors

      :component-did-mount               ;; the name of a lifecycle function
        (fn [this]
          (render-canvas)
          (println "component-did-mount")) ;; your implementation

       :component-did-update              ;; the name of a lifecycle function
        (fn [this old-argv]                ;; reagent provides you the entire "argv", not just the "props"
          (print "update")
          (doseq [dragRef @drag-ref] ; We delete each drag ref so that we dont overload after a couple of drags
            (.destroy dragRef))      ; if we don't then each new one doubles it and we get 100's of drag events
          (reset! drag-ref [])
          (let [elems (array-seq (.getElementsByClassName js/document "draggable"))]
            (doseq [elem elems]
              (swap! drag-ref conj
                (displace elem
                  (clj->js
                    {
                     :onMouseDown onMoveHandler
                     ; :onTouchStart onMoveEventStart
                     ; :onTouchMove handleBounds
                     ; :onMouseMove handleBounds
                     :onMouseUp onMoveEndHandler
                     ; :onTouchStop onMoveEventEnd
                    })))))
        )

        ;; other lifecycle funcs can go in here
        :reagent-render        ;; Note:  is not :render
         (fn [currentProject currentBoard linkStartId]           ;; remember to repeat parameters
           [:div.CanvasParent
             [Controls (:_id currentProject) currentBoard]
             [:div#Canvas
              (if currentBoard
                (for [storypoint (get-current-board-storypoints (:storypoints currentProject) currentBoard)] ; TODO actually pull the current
                  [:div {:key (:id storypoint)}
                    [Storypoint storypoint]]))]])}))

; TODO we can probably just work with the abpve canvas - remove this and import the component
(defn render [currentProject currentBoard linkStartId]
   [Canvas currentProject currentBoard linkStartId])

