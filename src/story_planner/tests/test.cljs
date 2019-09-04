(ns story-planner.tests.test
  (:require [cljs.test :refer-macros [deftest is testing run-tests]]
            [story-planner.services.scripts.folders :refer [get-folders-by-type]]))



(deftest test-numbers
  (is (= 1 1)))

(def test-folder-data [
  {:name "test" :type "entity"}
  {:name "test2" :type "board"}
  {:name "test3" :type "entity"}
  {:name "test4" :type "board"}
  {:name "test5" :type "entity"}
  {:name "test6" :type "board"}
])

(def test-folder-data-grouped {
  :entity [{:name "test" :type "entity"} {:name "test3" :type "entity"} {:name "test5" :type "entity"}]
  :board [{:name "test2" :type "board"} {:name "test4" :type "board"} {:name "test6" :type "board"}]})

(deftest test-folders
  (is (= test-folder-data-grouped (get-folders-by-type test-folder-data))))