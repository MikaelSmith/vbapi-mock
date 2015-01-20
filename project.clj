(defproject vbapi-mock "0.1.0-SNAPSHOT"
  :description "A mock of the Voicebox API v1"
  :url "http://vbapi-mock.herokuapp.com"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [environ "0.5.0"]
                 [org.clojure/tools.logging "0.3.1"]]
  :plugins [[environ/environ.lein "0.2.1"]
            [lein-ring "0.8.13"]]
  :ring {:handler vbapi-mock.core.handler/app}
  :hooks [environ.leiningen.hooks]
  :uberjar-name "vbapi-mock-standalone.jar"
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring-mock "0.1.5"]]}
   :production {:env {:production true}}
   :uberjar {:aot :all}}
  :main vbapi-mock.core.handler
  )
