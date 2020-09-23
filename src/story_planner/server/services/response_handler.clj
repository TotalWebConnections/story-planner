(ns story-planner.server.services.response-handler)


;TODO probably put some stuff here to force type to be specific error or sucess
(defn wrap-response [type data]
  "custom http response wrapper that we can use to decide UI actions"
  {:type type :data data})




