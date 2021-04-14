(ns story-planner.services.scripts.navigation)


; There's probably a way to do this with out current router
; but it gives undefined routes in components need to find
; a way to expose the routes app wide
; in the meantime this works and doesn't appear
; to reset our WS so should be good
(defn navigate [route]
  (set! (.. js/window -location -href) (str "/#/" route)))

(def transition-speed 500)
(def moving-frequency 5)

(defn doc-top []
  (+
    (.. js/document -body -scrollTop)
    (.. js/document -documentElement -scrollTop)))

(defn element-top [elem top]
  (if (.-offsetParent elem)
    (let [top (or (.-clientTop elem) 0)
          offset (.-offsetTop elem)]
      (+ top offset (element-top (.-offsetParent elem) top)))
    top))

(defn home-scroll [elem-id]
  (let [elem (.getElementById js/document elem-id)
        jumps (/ transition-speed moving-frequency)
        doc-top (doc-top)
        gap (/ (- (element-top elem 0) doc-top) jumps)]
    (doseq [i (range 1 (inc jumps))]
      (let [hop-top-pos (* gap i)
            move-to (+ hop-top-pos doc-top)
            timeout (* moving-frequency i)]
        (.setTimeout js/window (fn []
                                 (.scrollTo js/window 0 move-to))
                     timeout)))))
