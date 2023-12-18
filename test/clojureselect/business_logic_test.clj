(ns clojureselect.business-logic-test
  (:require [midje.sweet :refer :all]
            [clojureselect.business-logic :refer :all]))


;; (fact "Test for get-rating function"
;;       (get-ratings 1) => [{:id 1, :candidate-id 1, :job-id 1, :qualification-id 1, :value 10.0}
;;                           {:id 2, :candidate-id 1, :job-id 1, :qualification-id 2, :value 10.0}
;;                           {:id 3, :candidate-id 1, :job-id 1, :qualification-id 3, :value 10.0}])