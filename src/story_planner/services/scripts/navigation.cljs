(ns story-planner.services.scripts.navigation)


; There's probably a way to do this with out current router
; but it gives undefined routes in components need to find
; a way to expose the routes app wide
; in the meantime this works and doesn't appear
; to reset our WS so should be good
(defn navigate [route]
  (set! (.. js/window -location -href) (str "/#/" route)))