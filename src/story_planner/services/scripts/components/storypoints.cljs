(ns story-planner.services.scripts.components.storypoints)

(defn calcuate-point-on-curve [t p1 p2 p3 p4]
  "t(x) = (1-t)^3p1 +  3(1-t)^2tp2 + 3(1-t)t^2p3 + t^3p4 - where: 0 <= t <= 1
    at t(0) = our initial points
    at t(1) = our end point
    at t(.5) = roughly our midpoint - .5 is a close approximation"

    (+
      (* (- 1 t) (- 1 t) (- 1 t) p1)
      (* (- 1 t) (- 1 t) 3 t p2)
      (* (* t t)(- 1 t) 3 p3)
      (* t t t p4)))

(defn get-storypoint-by-id [storypoints id]
  "Returns match storypoint - first as ID should be unique"
  (first (filter (fn [storypoint]
    (if (= (:id storypoint) id) true false)) storypoints)))

(defn calculate-curve-x-initial [size starting-direction]
  "Gets the initial X position"
  (cond
    (= starting-direction "Top") (/ (:w size) 2)
    (= starting-direction "Bottom") (/ (:w size) 2)
    (= starting-direction "Left") 0
    (= starting-direction "Right") (:w size)))


(defn calculate-curve-y-initial [size starting-direction]
  "Gets the initial Y position"
  (cond
    (= starting-direction "Top") 0
    (= starting-direction "Bottom") (:h size)
    (= starting-direction "Left") (/ (:h size) 2)
    (= starting-direction "Right") (/ (:h size) 2)))

;size = size of Linked To Post
;position1 = position of the Linked To Post
;position2 = position of the linking post
(defn calculate-curve-x-end [size position1 position2 starting-direction]
  "Gets the end X position"
  (cond
    (= starting-direction "Top") (- (+ (:x position1) (* 0.5 (:w size))) (:x position2)) ;current x + 1/2 width - x size1
    (= starting-direction "Bottom") (- (+ (:x position1) (* 0.5 (:w size))) (:x position2)) ;^
    (= starting-direction "Left") (- (+ (:x position1) (:w size)) (:x position2)) ;current x - xsize one
    (= starting-direction "Right") (- (:x position1) (:x position2)))) ;current x + width - x size1

(defn calculate-curve-y-end [size position1 position2 starting-direction]
  "Gets the end Y position"
  (cond
    (= starting-direction "Top") (- (+ (:h size) (:y position1)) (:y position2)) ; height + y pos - pos 2 negitive to go up
    (= starting-direction "Bottom") (- (:y position1) (:y position2) ) ; y post - y position2
    (= starting-direction "Left") (- (+ (:y position1) (* 0.5 (:h size))) (:y position2)) ; 1/2 + pos y - y position2
    (= starting-direction "Right") (- (+ (:y position1)(* 0.5 (:h size))) (:y position2)))) ; 1/2 + pos - y position 2


; CONTROL POINT CALCULATORS
(defn caculate-first-control-point-x [direction distance xStart]
  (cond
    (= direction "Top") (- xStart 5)  ; start x - 5
    (= direction "Bottom") (+ xStart 5) ; start x + 5
    (= direction "Left") (/ distance 4)  ;1/4 distance
    (= direction "Right") (+ xStart (/ distance 4)) ;1/4 distance
  ))

(defn caculate-second-control-point-x [direction distance xStart]
  (cond
    (= direction "Top") (+ xStart 5)  ; start x + 5
    (= direction "Bottom") (- xStart 5) ; start x - 5
    (= direction "Left") (* 3 (/ distance 4))  ;3/4 distance
    (= direction "Right") (+ xStart  (* 3 (/ distance 4)) );3/4 distance
  ))

(defn caculate-first-control-point-y [direction distance yStart]
  (cond
    (= direction "Top") (/ distance 4)
    (= direction "Bottom") (+ yStart (/ distance 4))
    (= direction "Left") (- yStart 5)
    (= direction "Right") (+ yStart 5)
  ))

(defn caculate-second-control-point-y [direction distance yEnd]
  (cond
    (= direction "Top") (* 3 (/ distance 4))
    (= direction "Bottom") (- yEnd (* 3 (/ distance 4)))
    (= direction "Left") (+ yEnd 5)
    (= direction "Right") (- yEnd 5)
  ))
