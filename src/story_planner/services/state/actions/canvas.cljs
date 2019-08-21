(ns story-planner.services.state.actions.canvas)


(defn set-canvas-render [app-state value]
  (swap! app-state conj (:canvasLoaded value)))