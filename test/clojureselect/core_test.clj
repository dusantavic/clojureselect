(ns clojureselect.core-test
  (:require [clojure.test :refer :all]
            [clojureselect.business-logic :refer :all]
            [dk.ative.docjure.spreadsheet :refer [load-workbook]]))

;***********************************************************
;                 STABLO ODLUCIVANJA - TESTOVI
;***********************************************************


(use 'dk.ative.docjure.spreadsheet)

(deftest entropy-test-1
  (testing "Testing entropy function")
  (let [training-data [{:zaduzenje "kriticno"
                        :primanja "visoka"
                        :stan "da"
                        :otplata "ne"},
                       {:zaduzenje "kriticno"
                        :primanja "srednja"
                        :stan "ne"
                        :otplata "ne"},
                       {:zaduzenje "kriticno"
                        :primanja "niska"
                        :stan "da"
                        :otplata "ne"},
                       {:zaduzenje "kriticno"
                        :primanja "visoka"
                        :stan "ne"
                        :otplata "ne"},
                       {:zaduzenje "prihvatljivo"
                        :primanja "visoka"
                        :stan "da"
                        :otplata "da"},
                       {:zaduzenje "prihvatljivo"
                        :primanja "niska"
                        :stan "da"
                        :otplata "da"},
                       {:zaduzenje "prihvatljivo"
                        :primanja "srednja"
                        :stan "da"
                        :otplata "da"},
                       {:zaduzenje "prihvatljivo"
                        :primanja "srednja"
                        :stan "ne"
                        :otplata "ne"},
                       {:zaduzenje "povoljno"
                        :primanja "niska"
                        :stan "ne"
                        :otplata "da"},
                       {:zaduzenje "povoljno"
                        :primanja "niska"
                        :stan "ne"
                        :otplata "ne"},
                       {:zaduzenje "povoljno"
                        :primanja "niska"
                        :stan "ne"
                        :otplata "ne"}]]
(is (= (round-to-decimal-places (entropy (extract-column training-data :otplata)) 4) 0.9457))))

(entropy-test-1)


(deftest information-gain-test
  (testing "Testing information-gain function"
    (let [training-data [{:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "niska"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "niska"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "da"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"}]]
      (is (= (round-to-decimal-places (information-gain training-data :stan :otplata) 4) 0.1498)))
    ))

(information-gain-test)

(deftest information-gain-test-2
  (testing "Testing information-gain function"
    (let [training-data [{:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "niska"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "niska"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "da"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"}]]
      (is (= (round-to-decimal-places (information-gain training-data :primanja :otplata) 4) 0.0034)))))


(information-gain-test-2)

(deftest tree-test-1
  (testing "Testing decision tree and predictions"
    (let [attributes [:education, :work-experience, :technical-skills, :soft-skills,
                      :references, :communication-skills, :problem-solving-ability,
                      :cultural-fit, :learning-ability]
          data (into [] (->> (load-workbook "resources/candidates.xlsx")
                             (select-sheet "candidates")
                             (select-columns {:A :education, :B :work-experience,
                                              :C :technical-skills, :D :soft-skills,
                                              :E :references, :F :communication-skills,
                                              :G :problem-solving-ability, :H :cultural-fit,
                                              :I :learning-ability, :J :job-fit})
                             rest))
          tree (create-tree data :job-fit attributes)
          entity {:education "High School",
                  :work-experience "Beginner",
                  :technical-skills "Intermediate",
                  :soft-skills "Medium",
                  :references "Yes",
                  :communication-skills "Excellent",
                  :problem-solving-ability "Low",
                  :cultural-fit "High Fit",
                  :learning-ability "High"}]
      (is (= (tree-predict tree entity) "Good Fit")))))

(tree-test-1)

(deftest tree-test-2
  (testing "Testing decision tree and predictions"
    (let [attributes [:education, :work-experience, :technical-skills, :soft-skills,
                      :references, :communication-skills, :problem-solving-ability,
                      :cultural-fit, :learning-ability]
          data (into [] (->> (load-workbook "resources/candidates.xlsx")
                             (select-sheet "candidates")
                             (select-columns {:A :education, :B :work-experience,
                                              :C :technical-skills, :D :soft-skills,
                                              :E :references, :F :communication-skills,
                                              :G :problem-solving-ability, :H :cultural-fit,
                                              :I :learning-ability, :J :job-fit})
                             rest))
          tree (create-tree data :job-fit attributes)
          entity {:education "High School",
                  :work-experience "Senior",
                  :technical-skills "Advanced",
                  :soft-skills "Medium",
                  :references "No",
                  :communication-skills "Good",
                  :problem-solving-ability "High",
                  :cultural-fit "High Fit",
                  :learning-ability "Medium"}]
      (is (= (tree-predict tree entity) "High Fit")))))

(tree-test-2)

(deftest predict-many-test
  (testing "Testing function tree-predict-many"
    (let [attributes [:education, :work-experience, :technical-skills, :soft-skills,
                      :references, :communication-skills, :problem-solving-ability,
                      :cultural-fit, :learning-ability]
          data (into [] (->> (load-workbook "resources/candidates.xlsx")
                             (select-sheet "candidates")
                             (select-columns {:A :education, :B :work-experience,
                                              :C :technical-skills, :D :soft-skills,
                                              :E :references, :F :communication-skills,
                                              :G :problem-solving-ability, :H :cultural-fit,
                                              :I :learning-ability, :J :job-fit})
                             rest))
          tree (create-tree data :job-fit attributes)
          entities [{:education "High School",
                     :work-experience "Beginner",
                     :technical-skills "Intermediate",
                     :soft-skills "Medium",
                     :references "Yes",
                     :communication-skills "Excellent",
                     :problem-solving-ability "Low",
                     :cultural-fit "High Fit",
                     :learning-ability "High"},
                    {:education "Bachelor's degree",
                     :work-experience "Beginner",
                     :technical-skills "Beginner",
                     :soft-skills "Low",
                     :references "No",
                     :communication-skills "Excellent",
                     :problem-solving-ability "Low",
                     :cultural-fit "High Fit",
                     :learning-ability "High"},
                    {:education "High School",
                     :work-experience "Beginner",
                     :technical-skills "Beginner",
                     :soft-skills "Low",
                     :references "No",
                     :communication-skills "Good",
                     :problem-solving-ability "Low",
                     :cultural-fit "Low Fit",
                     :learning-ability "High"}]
          expected [{:work-experience "Beginner",
                     :soft-skills "Medium",
                     :references "Yes",
                     :communication-skills "Excellent",
                     :learning-ability "High",
                     :job-fit "Good Fit",
                     :education "High School",
                     :cultural-fit "High Fit",
                     :technical-skills "Intermediate",
                     :problem-solving-ability "Low"}
                    {:work-experience "Beginner",
                     :soft-skills "Low",
                     :references "No",
                     :communication-skills "Excellent",
                     :learning-ability "High",
                     :job-fit "Low Fit",
                     :education "Bachelor's degree",
                     :cultural-fit "High Fit",
                     :technical-skills "Beginner",
                     :problem-solving-ability "Low"}
                    {:work-experience "Beginner",
                     :soft-skills "Low",
                     :references "No",
                     :communication-skills "Good",
                     :learning-ability "High",
                     :job-fit "Low Fit",
                     :education "High School",
                     :cultural-fit "Low Fit",
                     :technical-skills "Beginner",
                     :problem-solving-ability "Low"}]]
      (is (= (tree-predict-many tree entities) expected)))))

(predict-many-test)

(deftest accuracy-test
  (testing "Testing accuracy function"
    (let [data (into [] (->> (load-workbook "resources/candidates.xlsx")
                             (select-sheet "candidates")
                             (select-columns {:A :education, :B :work-experience,
                                              :C :technical-skills, :D :soft-skills,
                                              :E :references, :F :communication-skills,
                                              :G :problem-solving-ability, :H :cultural-fit,
                                              :I :learning-ability, :J :job-fit})))
          training-and-validation (training-and-validation data 0.8)
          training-data (get training-and-validation 0)
          validation-data (get training-and-validation 1)
          attributes [:education, :work-experience, :technical-skills, :soft-skills,
                      :references, :communication-skills, :problem-solving-ability,
                      :cultural-fit, :learning-ability]
          tree (create-tree training-data :job-fit attributes)
          predictions (tree-predict-many tree (remove-column validation-data :job-fit))]
      (is (= (round-to-decimal-places (calculate-accuracy validation-data predictions) 1) 0.6)))))

(accuracy-test)

(deftest best-attribute-test
  (testing "Testing best-attribute function"
    (let [training-data [{:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "niska"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "niska"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "da"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"}]]
      (is (= (best-attribute training-data :otplata [:zaduzenje :primanja :stan]) :zaduzenje)))))

(best-attribute-test)

(deftest create-tree-test
  (testing "Testing create-tree function"
    (let [training-data [{:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "niska"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "niska"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "da"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"}]]
      (is (= (create-tree training-data :otplata [:zaduzenje :primanja :stan])
             {:zaduzenje {"kriticno" "ne", "prihvatljivo" {:stan {"da" "da", "ne" "ne"}}, "povoljno" "ne"}})))))

(create-tree-test)

(deftest prediction-test-1
  (testing "Testing tree-predict function"
    (let [training-data [{:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "niska"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "niska"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "da"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"}]
                          tree (create-tree training-data :otplata [:zaduzenje :primanja :stan]) 
                          test-entity {:zaduzenje "kriticno"
                                             :primanja "visoka"
                                             :stan "da"}]
      (is (= (tree-predict tree test-entity) "ne")))))

(prediction-test-1)

(deftest prediction-test-2
  (testing "Testing tree-predict function"
    (let [training-data [{:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "niska"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "niska"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "da"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"}]
          tree (create-tree training-data :otplata [:zaduzenje :primanja :stan])
          test-entity {:zaduzenje "kriticno"
                       :primanja "niska"
                       :stan "ne"}]
      (is (= (tree-predict tree test-entity) "ne")))))

(prediction-test-2)

(deftest prediction-test-3
  (testing "Testing tree-predict function"
    (let [training-data [{:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "niska"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "niska"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "da"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"}]
          tree (create-tree training-data :otplata [:zaduzenje :primanja :stan])
          test-entity {:zaduzenje "prihvatljivo"
                       :primanja "niska"
                       :stan "da"}]
      (is (= (tree-predict tree test-entity) "da")))))

(prediction-test-3)

(deftest training-and-validation-test
  (testing "Testing training-and-validation function"
    (let [data [{:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "niska"
                          :stan "da"
                          :otplata "ne"},
                         {:zaduzenje "kriticno"
                          :primanja "visoka"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "visoka"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "niska"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "da"
                          :otplata "da"},
                         {:zaduzenje "prihvatljivo"
                          :primanja "srednja"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "da"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"},
                         {:zaduzenje "povoljno"
                          :primanja "niska"
                          :stan "ne"
                          :otplata "ne"}]
      expected [[{:zaduzenje "kriticno", :primanja "visoka", :stan "da", :otplata "ne"}
                 {:zaduzenje "kriticno", :primanja "srednja", :stan "ne", :otplata "ne"}
                 {:zaduzenje "kriticno", :primanja "niska", :stan "da", :otplata "ne"}
                 {:zaduzenje "kriticno", :primanja "visoka", :stan "ne", :otplata "ne"}
                 {:zaduzenje "prihvatljivo", :primanja "visoka", :stan "da", :otplata "da"}
                 {:zaduzenje "prihvatljivo", :primanja "niska", :stan "da", :otplata "da"}
                 {:zaduzenje "prihvatljivo", :primanja "srednja", :stan "da", :otplata "da"}
                 {:zaduzenje "prihvatljivo", :primanja "srednja", :stan "ne", :otplata "ne"}
                 {:zaduzenje "povoljno", :primanja "niska", :stan "ne", :otplata "da"}]
                [{:zaduzenje "povoljno", :primanja "niska", :stan "ne", :otplata "ne"}
                 {:zaduzenje "povoljno", :primanja "niska", :stan "ne", :otplata "ne"}]]] 
      (is (= (training-and-validation data 0.8) expected)))))

(training-and-validation-test)


(deftest log2-test
  (testing "Testing log2 function"
    (is (= (log2 4) 2.0))))

(log2-test)

(deftest log2-test2
  (testing "Testing log2 function"
    (is (= (round-to-decimal-places (log2 144) 4) 7.1699))))

(log2-test2)

(deftest extract-column-test
  (testing "Testing extract-column function"
    (is (= (extract-column [{:name "dusan"
                             :age 23}, 
                            {:name "nevena"
                             :age 23}
                            {:name "arsenije"
                             :age 20}] :age) [23 23 20]))))

(extract-column-test)

(deftest remove-column-test
  (testing "Testing remove-column function"
    (is (= (remove-column [{:name "dusan"
                            :age 23},
                           {:name "nevena"
                            :age 23}
                           {:name "arsenije"
                            :age 20}] :age) [{:name "dusan"} {:name "nevena"} {:name "arsenije"}]))))

(remove-column-test)

(deftest remove-column-test-2
  (testing "Testing remove-column function"
    (let [data [{:education "High School",
                 :work-experience "Beginner",
                 :technical-skills "Intermediate",
                 :soft-skills "Medium",
                 :references "Yes",
                 :communication-skills "Excellent",
                 :problem-solving-ability "Low",
                 :cultural-fit "High Fit",
                 :learning-ability "High"},
                {:education "Bachelor's degree",
                 :work-experience "Beginner",
                 :technical-skills "Beginner",
                 :soft-skills "Low",
                 :references "No",
                 :communication-skills "Excellent",
                 :problem-solving-ability "Low",
                 :cultural-fit "High Fit",
                 :learning-ability "High"},
                {:education "High School",
                 :work-experience "Beginner",
                 :technical-skills "Beginner",
                 :soft-skills "Low",
                 :references "No",
                 :communication-skills "Good",
                 :problem-solving-ability "Low",
                 :cultural-fit "Low Fit",
                 :learning-ability "High"}]
          expected [{:work-experience "Beginner",
                     :references "Yes",
                     :communication-skills "Excellent",
                     :learning-ability "High",
                     :education "High School",
                     :cultural-fit "High Fit",
                     :technical-skills "Intermediate",
                     :problem-solving-ability "Low"}
                    {:work-experience "Beginner",
                     :references "No",
                     :communication-skills "Excellent",
                     :learning-ability "High",
                     :education "Bachelor's degree",
                     :cultural-fit "High Fit",
                     :technical-skills "Beginner",
                     :problem-solving-ability "Low"}
                    {:work-experience "Beginner",
                     :references "No",
                     :communication-skills "Good",
                     :learning-ability "High",
                     :education "High School",
                     :cultural-fit "Low Fit",
                     :technical-skills "Beginner",
                     :problem-solving-ability "Low"}]]
      (is (= (into [] (remove-column data :soft-skills)) expected)))))

(remove-column-test-2)

(deftest index-of-max-test
  (testing "Testing index-of-max function"
    (is (= (index-of-max [0 4 144 7 -8 12 10 14 -70 78]) 2))))

(index-of-max-test)




(def data [{:zaduzenje "kriticno"
                                   :primanja "visoka"
                                   :stan "da"
                                   :otplata "ne"},
                                  {:zaduzenje "kriticno"
                                   :primanja "srednja"
                                   :stan "ne"
                                   :otplata "ne"},
                                  {:zaduzenje "kriticno"
                                   :primanja "niska"
                                   :stan "da"
                                   :otplata "ne"},
                                  {:zaduzenje "kriticno"
                                   :primanja "visoka"
                                   :stan "ne"
                                   :otplata "ne"},
                                  {:zaduzenje "prihvatljivo"
                                   :primanja "visoka"
                                   :stan "da"
                                   :otplata "da"},
                                  {:zaduzenje "prihvatljivo"
                                   :primanja "niska"
                                   :stan "da"
                                   :otplata "da"},
                                  {:zaduzenje "prihvatljivo"
                                   :primanja "srednja"
                                   :stan "da"
                                   :otplata "da"},
                                  {:zaduzenje "prihvatljivo"
                                   :primanja "srednja"
                                   :stan "ne"
                                   :otplata "ne"},
                                  {:zaduzenje "povoljno"
                                   :primanja "niska"
                                   :stan "ne"
                                   :otplata "da"},
                                  {:zaduzenje "povoljno"
                                   :primanja "niska"
                                   :stan "ne"
                                   :otplata "ne"},
                                  {:zaduzenje "povoljno"
                                   :primanja "niska"
                                   :stan "ne"
                                   :otplata "ne"}])

















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


(deftest job-name-test
  (testing "Testing get-jobs-name function"
    (is (= (get-jobs-name -40) nil))))

(deftest job-name-test2
  (testing "Testing get-jobs-name function"
    (is (= (get-jobs-name 1) "C# Junior Developer"))))

(deftest ponder-test
  (testing "Testing get-ponder function"
    (is (= (get-ponder 1 1) 0.5))))

(deftest ponder-test2
  (testing "Testing get-ponder function"
    (is (> (get-ponder 1 1) 0))))

(deftest ponder-test3
  (testing "Testing get-ponder function"
    (is (= (get-ponder -1 -1) "error. please insert valid id values"))))

(deftest ponder-test4
  (testing "Testing get-ponder function"
    (is (= (get-ponder -1 1) "error. please insert valid id values"))))


(deftest ponder-test5
  (testing "Testing get-ponder function"
    (is (= (get-ponder 1 -1) "error. please insert valid id values"))))

(deftest ratings-of-criteria-test
  (testing "Testing get-ratings-of-criteria function"
    (is (= (get-ratings-of-criteria 1 1) [{:id 1, :candidate-id 1, :job-id 1, :qualification-id 1, :value 10.0}
                                          {:id 4, :candidate-id 2, :job-id 1, :qualification-id 1, :value 8.0}]))))

(deftest ratings-of-criteria-test2
  (testing "Testing get-ratings-of-criteria function"
    (is (= (get-ratings-of-criteria -1 -1) []))))

(deftest ratings-of-criteria-test3
  (testing "Testing get-ratings-of-criteria function"
    (is (= (get-ratings-of-criteria -1 1) []))))

(deftest ratings-of-criteria-test4
  (testing "Testing get-ratings-of-criteria function"
    (is (= (get-ratings-of-criteria 1 -1) []))))


(deftest ratings-of-job-test
  (testing "Testing get-ratings-of-job function"
    (is (= (get-ratings-of-job -1) []))))


(deftest ratings-of-job-test2
  (testing "Testing get-ratings-of-job function"
    (is (= (get-ratings-of-job 1) [{:id 1, :candidate-id 1, :job-id 1, :qualification-id 1, :value 10.0}
                                   {:id 2, :candidate-id 1, :job-id 1, :qualification-id 2, :value 10.0}
                                   {:id 3, :candidate-id 1, :job-id 1, :qualification-id 3, :value 10.0}
                                   {:id 4, :candidate-id 2, :job-id 1, :qualification-id 1, :value 8.0}
                                   {:id 5, :candidate-id 2, :job-id 1, :qualification-id 2, :value 9.1}
                                   {:id 6, :candidate-id 2, :job-id 1, :qualification-id 3, :value 7.0}]))))
