(ns story-planner.dev-server)

;; define index content
(def home
  "<!DOCTYPE html>
<html>
  <head>
    <meta charset=\"UTF-8\">
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">
    <link href=\"css/style.css\" rel=\"stylesheet\" type=\"text/css\">
    <link rel=\"icon\" href=\"https://clojurescript.org/images/cljs-logo-icon-32.png\">
  </head>
  <body>
    <div id=\"app\"></div>
    <script src=\"/cljs-out/dev-main.js\" type=\"text/javascript\"></script>
  </body>
</html>")

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (slurp (clojure.java.io/resource "public/index.html"))})

