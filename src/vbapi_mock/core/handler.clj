(ns vbapi-mock.core.handler
  (:require [compojure.core :refer [defroutes GET POST DELETE]]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.json :as middleware]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [clj-http.client :as client]))

(def vbapi-room-code (env :vbapi-room-code))
(def vbapi-version "v1")

(defn valid_room? [room_code] (= room_code vbapi-room-code))
(defn as_room [room_code action]
  (if (valid_room? room_code)
    (action)
    {:error "Invalid room_code", :status 401}))

(defn get-queue [{room_code :room_code}]
  (as_room room_code
           #(hash-map
              :room_code room_code,
              :current_song {
                             :song_id 68250,
                             :play_id "G-Test-6c4008b3e6c4-1435717763903-2",
                             :title "Beautiful, Dirty, Rich",
                             :artist "Lady Gaga",
                             :duration 170000,
                             :position 30000,
                             :paused false
                             },
              :songs_queued 3,
              :queue [
                      {
                       :index 0,
                       :song_id 69001,
                       :play_id "G-Test-6c4008b3e6c4-1435717770940-3",
                       :title "Summerboy",
                       :artist "Lady Gaga",
                       :duration 242865
                       }
                      {
                       :index 1,
                       :song_id 69002,
                       :play_id "G-Test-6c4008b3e6c4-1435717770940-2",
                       :title "Pokerface",
                       :artist "Lady Gaga",
                       :duration 210000
                       }
                      {
                       :index 2,
                       :song_id 69003,
                       :play_id "G-Test-6c4008b3e6c4-1435717770940-2",
                       :title "Shake It Off",
                       :artist "Taylor Swift",
                       :duration 280000
                       }
                      ]
              )))

(defn post-queue [{room_code :room_code,
                   song_id :song_id,
                   to :to,
                   allow_duplicate :allow_duplicate,
                   message :message,
                   message_color :message_color}]
  (as_room room_code
           #(if song_id
              (do
                (log/infof "Adding sing %s to room %s" song_id room_code)
                {
                 :index 2,
                 :song_id 67277,
                 :play_id "G-Southeast-6c4008b3e6c4-1435717896331-4",
                 :title "Don't Stop Believin'",
                 :artist "Journey",
                 :duration 268128
                 })
              {:error "Missing parameter: song_id"
               :status 400})))

(defn del-queue [{room_code :room_code, from :from}]
  (as_room room_code
           #(hash-map :action (str "deleted " (or from "queue")))))

(defn reorder-queue [{room_code :room_code, from :from, to :to}]
  (as_room room_code
           #(if from
              {:action (str "move from " from " to " (or to 0))}
              {:error "Missing parameter: from"
               :status 400})))

(defn search-songs [{query :query}]
  (if query
    (do
     (log/infof "Searching for songs matching %s" query)
     (:body (client/get (str "http://vbsongs.com/api/" vbapi-version "/songs/search")
                        {:query-params {:query query}
                         :accept :json
                         :throw-exceptions false})))))

(defn wrap-response [resp]
  (if-let [stat (:status resp)]
    {:status stat :body resp}
    {:body resp}))

(defroutes app-routes
  (GET "/ping" [] "pong")
  (GET (str "/api/" vbapi-version "/queue") {params :params} (wrap-response (get-queue params)))
  (POST (str "/api/" vbapi-version "/queue") {params :params} (wrap-response (post-queue params)))
  (DELETE (str "/api/" vbapi-version "/queue") {params :params} (wrap-response (del-queue params)))
  (POST (str "/api/" vbapi-version "/queue/reorder") {params :params} (wrap-response (reorder-queue params)))
  (GET (str "/api/" vbapi-version "/songs/search") {params :params} (wrap-response (search-songs params)))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-params)
      (middleware/wrap-json-response)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 5000))]
    (jetty/run-jetty (handler/api #'app) {:port port :join? false})))
