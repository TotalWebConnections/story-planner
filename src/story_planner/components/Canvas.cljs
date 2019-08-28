(ns story-planner.components.Canvas
  (:require [reagent.core :as reagent :refer [atom]]
            [story-planner.services.state.dispatcher :refer [handle-state-change]]
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
(reagent/after-render (fn []
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

  (defn onMoveEndHandler[]
    (.resume panHandler))

  (.draggable (interact ".draggable") (clj->js {:inertia false :onmove onMoveHandler :onend onMoveEndHandler})))
)))

(defn render []
  [:div.CanvasParent
    [:div#Canvas
      [:div.card.draggable [:p "im some content in the canvas"]]]])