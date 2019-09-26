(ns story-planner.components.Canvas
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.scripts.api.api :as api]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
            [story-planner.components.canvas.Controls :refer [Controls]]
            [story-planner.services.scripts.canvas :refer [get-current-board-storypoints]]
            ["panzoom" :as panzoom]
            ["interactjs" :as interact]))

(defn getXVal [target event]
  "Grabs the x value for the onDrag event"
  (let [x (+ (js/parseFloat (.getAttribute target "data-x")) (.-dx event))]
    (if (js/isNaN x)
      0
      x)))

(defn getYVal [target event]
  "Grabs the y value for the onDrag event"
  (let [y (+ (js/parseFloat (.getAttribute target "data-y")) (.-dy event))]
    (if (js/isNaN y)
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

  ; function taken from interact - probably not `functional`
  ; TODO we should chanage drag speed based on zoom level
  ; The further out the faster the zoom needs to be to seem fluid
  (defn onMoveHandler [event]
    (.pause panHandler) ; resume call on end on end
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
                                       :id (.getAttribute target "id")}))
    (.resume panHandler))

  (.draggable (interact ".draggable") (clj->js {:inertia false :onmove onMoveHandler :onend onMoveEndHandler})))
))

(defn Canvas [currentProject currentBoard]
  (reagent/create-class                 ;; <-- expects a map of functions
    {:display-name  "canvas"      ;; for more helpful warnings & errors

      :component-did-mount               ;; the name of a lifecycle function
        (fn [this]
          (render-canvas)
          (println "component-did-mount")) ;; your implementation

       :component-did-update              ;; the name of a lifecycle function
        (fn [this old-argv]                ;; reagent provides you the entire "argv", not just the "props"
          (js/console.log "did update"))

        ;; other lifecycle funcs can go in here


        :reagent-render        ;; Note:  is not :render
         (fn [currentProject currentBoard]           ;; remember to repeat parameters
           [:div.CanvasParent
             [Controls (:_id currentProject) currentBoard]
             [:div#Canvas
              (if currentBoard
                (for [storypoint (get-current-board-storypoints (:storypoints currentProject) currentBoard)] ; TODO actually pull the current
                  [:div.card.draggable {:key (:id storypoint) :id (:id storypoint)
                                        :data-x (:x (:position storypoint))
                                        :data-y (:y (:position storypoint))
                                        :style {:transform (str "translate("(:x (:position storypoint))"px,"(:y (:position storypoint))"px)")}}
                    [:h2 (:name storypoint)]
                    [:p (:description storypoint)]]))]])}))

; TODO we can probably just work with the abpve canvas - remove this and import the component
(defn render [currentProject currentBoard]
   [Canvas currentProject currentBoard])

