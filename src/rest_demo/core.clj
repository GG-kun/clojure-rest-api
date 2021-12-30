  (ns rest-demo.core
    (:use clojure.data)
    (:require [org.httpkit.server :as server]
              [compojure.core :refer :all]
              [compojure.route :as route]
              [ring.middleware.defaults :refer :all]
              [clojure.pprint :as pp]
              [clojure.string :as str]
              [clojure.data.json :as json])
    (:gen-class))

; my people-collection mutable collection vector
(def element {:id "id" :gay "pene"})
(def notelement {:id "id2" :gay "vagina"})
(def people-collection [notelement element])

;; Find
(defn all [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (str (json/write-str people-collection))})

;; FindOne
(defn element-found [f element]
  (not (nth (diff f element) 0 true)))

(defn filter-collection [f collection] 
  (nth (for [element collection
    :let [y (element-found f element)]
    :when y]
  element) 0 nil))

(defn getparameter [req pname] (get (:params req) pname))

; get-by-id
(defn get-by-id [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (str (json/write-str (filter-collection (get req :params) people-collection)))})

;; Add
(defn add [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (str "functional")})
  ;;  :body    (str (json/write-str (get req :body)))})

(defroutes app-routes
  (GET "/" [] (all))
  (GET "/:id" [] (get-by-id))
  (POST "/" {body :body} (add body))
  ;; (PUT "/:id" [] edit)
  (route/not-found "Error, page not found!"))

  (defn -main
    [& args]
    (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
      ; Run the server with Ring.defaults middleware
      (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
      ; Run the server without ring defaults
      ;(server/run-server #'app-routes {:port port})
      (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
