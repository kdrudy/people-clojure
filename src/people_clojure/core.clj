(ns people-clojure.core
  (:require [clojure.string :as str]
            [compojure.core :as c]
            [ring.adapter.jetty :as j]
            [hiccup.core :as h])
  (:gen-class))




(defn read-people []
  (let [people (slurp "people.csv")
        people (str/split-lines people)
        people (map (fn [line]
                      (str/split line #","))
                 people)
        header (first people)
        people (rest people)
        people (map (fn [line]
                      (zipmap header line))
                 people)]
    people))

(defn people-html [country]
  (let [people (read-people)
        people (if (= 0 (count country))
                 people
                 (filter (fn [person]
                           (= (get person "country") country)) 
                     people))]
    [:ol
     (map (fn [person]
            [:li (str (get person "first_name") " " 
                   (get person "last_name"))])
       people)]))
     

(c/defroutes app
  (c/GET "/:country{.*}" [country]
    (h/html [:html
             [:body
              (people-html country)]])))

(defonce server (atom nil))

(defn -main []
  (when @server
    (.stop @server))
  (reset! server (j/run-jetty app {:port 3000 :join? false})))







