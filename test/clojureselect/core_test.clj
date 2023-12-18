(ns clojureselect.core-test
  (:require [clojure.test :refer :all]
            [clojureselect.business-logic :refer :all]))

;; (deftest a-test
;;   (testing "FIXME, I fail."
;;     (is (= 0 1))))

;***********************************************************
;                     OSNOVNE FUNKCIJE 
;***********************************************************

(deftest rating-test
  (testing "Testing get-ratings function"
  (is (= (get-ratings 1) [{:id 1, :candidate-id 1, :job-id 1, :qualification-id 1, :value 10.0}
                          {:id 2, :candidate-id 1, :job-id 1, :qualification-id 2, :value 10.0}
                          {:id 3, :candidate-id 1, :job-id 1, :qualification-id 3, :value 10.0}]))))

(deftest rating-test2
  (testing "Testing get-ratings function"
    (is (= (get-ratings 2) [{:id 4, :candidate-id 2, :job-id 1, :qualification-id 1, :value 8.0}
                            {:id 5, :candidate-id 2, :job-id 1, :qualification-id 2, :value 9.1}
                            {:id 6, :candidate-id 2, :job-id 1, :qualification-id 3, :value 7.0}]))))

(deftest rating-test3
  (testing "Testing get-ratings function"
    (is (= (get-ratings 180) []))))

(deftest rating-test4
  (testing "Testing get-ratings function"
    (is (= (get-ratings -7) []))))


(deftest candidates-test
  (testing "Testing get-candidates function"
    (is (= (get-candidates 1) [{:id 1, :firstname "Dusan", :lastname "Tavic", :active true, :email "dusantavic1@gmail.com", :status 0, :job-id 1}
                               {:id 2, :firstname "Nenad", :lastname "Panovic", :active true, :email "nenadpann@gmail.com", :status 0, :job-id 1}
                               {:id 4,
                                :firstname "Arsenije",
                                :lastname "Pavlovic",
                                :active true,
                                :email "arsenijee00@yahoo.com",
                                :status 0,
                                :job-id 1}]))))

(deftest candidates-test2
  (testing "Testing get-candidates function"
    (is (= (get-candidates 0) []))))

(deftest candidates-test3
  (testing "Testing get-candidates function"
    (is (= (get-candidates "dusan") []))))


(deftest jobs-criteria-test
  (testing "Testing get-jobs-criteria function"
    (is (= (get-jobs-criteria 1) [{:job-id 1, :qualification-id 1, :ponder 0.5}
                                  {:job-id 1, :qualification-id 2, :ponder 0.3}
                                  {:job-id 1, :qualification-id 3, :ponder 0.2}]))))

(deftest jobs-criteria-test2
  (testing "Testing get-jobs-criteria function"
    (is (= (get-jobs-criteria -10) []))))
