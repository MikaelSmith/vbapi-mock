(ns vbapi-mock.core.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [vbapi-mock.core.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/ping"))]
      (is (= (:status response) 200))
      (is (= (:body response) "pong"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
