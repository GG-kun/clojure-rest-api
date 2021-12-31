  (ns rest-demo.core
    (:use clojure.data)
    (:require [org.httpkit.server :as server]
              [compojure.core :refer :all]
              [compojure.route :as route]
              [ring.middleware.defaults :refer :all]
              [ring.middleware.reload :refer [wrap-reload]]
              [ring.middleware.json :as js]
              [clojure.pprint :as pp]
              [clojure.string :as str]
              [clojure.data.json :as json])
    (:gen-class))

; my elements mutable collection vector
(def elements (atom []))

;; Find
(defn all [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (str (json/write-str @elements))})

;; FindOne
(defn element-found [f element]
  (not (nth (diff f element) 0 true)))

(defn collection-find [f collection]
  (for [element collection
    :let [y (element-found f element)]
    :when y]
  element))

(defn find-one [f collection] 
  (nth (collection-find f collection) 0 nil))

; get-by-id
(defn get-by-id [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (str (json/write-str (find-one (get req :params) @elements)))})

;; Add
(defn add-element [element collection]
  (swap! collection conj element))

(defn add [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (add-element (get req :params) elements)})

(defroutes app-routes
  (GET "/" [] all)
  (GET "/:id" [] get-by-id)
  (POST "/" [] add)
  ;; (PUT "/:id" [] edit)
  ;; (DELETE "/:id" [] edit)
  (route/not-found "Error, page not found!"))

  (defn -main
    [& args]
    (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
      ; Run the server with Ring.defaults middleware
      (server/run-server (wrap-reload (js/wrap-json-params (js/wrap-json-response (wrap-defaults #'app-routes api-defaults)))) {:port port})
      ; Run the server without ring defaults
      ;(server/run-server #'app-routes {:port port})
      (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
