(ns clojureselect.core-test
  (:require [clojure.test :refer :all]
            [clojureselect.business-logic :refer :all]
            [dk.ative.docjure.spreadsheet :refer [load-workbook]]))

;***********************************************************
;                 STABLO ODLUCIVANJA - TESTOVI
;***********************************************************


(use 'dk.ative.docjure.spreadsheet)

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
      tree (create-tree data :job-fit attributes)]
  (print-tree tree 0))


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
      entity {:education "Postgraduate Education",
              :work-experience "Senior",
              :technical-skills "Intermediate",
              :soft-skills "Medium",
              :references "Yes",
              :communication-skills "Excellent",
              :problem-solving-ability "Low",
              :cultural-fit "High Fit",
              :learning-ability "High"}]
  (tree-predict tree entity))

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
      entity {:education "High School"
              :work-experience "Beginner",
              :technical-skills "Intermediate",
              :soft-skills "Medium",
              :references "Yes",
              :communication-skills "Excellent",
              :problem-solving-ability "Low",
              :cultural-fit "High Fit",
              :learning-ability "High"}]
  (tree-predict tree entity))

(let [training-data [{:debts "critical"
                      :income "high"
                      :apartment "yes"
                      :loan-repayment "no"},
                     {:debts "critical"
                      :income "medium"
                      :apartment "no"
                      :loan-repayment "no"},
                     {:debts "critical"
                      :income "low"
                      :apartment "yes"
                      :loan-repayment "no"},
                     {:debts "critical"
                      :income "high"
                      :apartment "no"
                      :loan-repayment "no"},
                     {:debts "acceptable"
                      :income "high"
                      :apartment "yes"
                      :loan-repayment "yes"},
                     {:debts "acceptable"
                      :income "low"
                      :apartment "yes"
                      :loan-repayment "yes"},
                     {:debts "acceptable"
                      :income "medium"
                      :apartment "yes"
                      :loan-repayment "yes"},
                     {:debts "acceptable"
                      :income "medium"
                      :apartment "no"
                      :loan-repayment "no"},
                     {:debts "good"
                      :income "low"
                      :apartment "no"
                      :loan-repayment "yes"},
                     {:debts "good"
                      :income "low"
                      :apartment "no"
                      :loan-repayment "no"},
                     {:debts "good"
                      :income "low"
                      :apartment "no"
                      :loan-repayment "no"}]
      tree (create-tree training-data :loan-repayment [:debts :income :apartment])]
  (print-tree tree 0))


(let [training-data [{:debts "critical"
                      :income "high"
                      :apartment "yes"
                      :loan-repayment "no"},
                     {:debts "critical"
                      :income "medium"
                      :apartment "no"
                      :loan-repayment "no"},
                     {:debts "critical"
                      :income "low"
                      :apartment "yes"
                      :loan-repayment "no"},
                     {:debts "critical"
                      :income "high"
                      :apartment "no"
                      :loan-repayment "no"},
                     {:debts "acceptable"
                      :income "high"
                      :apartment "yes"
                      :loan-repayment "yes"},
                     {:debts "acceptable"
                      :income "low"
                      :apartment "yes"
                      :loan-repayment "yes"},
                     {:debts "acceptable"
                      :income "medium"
                      :apartment "yes"
                      :loan-repayment "yes"},
                     {:debts "acceptable"
                      :income "medium"
                      :apartment "no"
                      :loan-repayment "no"},
                     {:debts "good"
                      :income "low"
                      :apartment "no"
                      :loan-repayment "yes"},
                     {:debts "good"
                      :income "low"
                      :apartment "no"
                      :loan-repayment "no"},
                     {:debts "good"
                      :income "low"
                      :apartment "no"
                      :loan-repayment "no"}]
      tree (create-tree training-data :loan-repayment [:debts :income :apartment])
      test-entity {:debts "critical"
                   :income "high"
                   :apartment "yes"}]
  (tree-predict tree test-entity))

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
      (is (= (round-to-decimal-places (information-gain training-data :stan :otplata) 4) 0.1498)))))

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

(deftest tree-test-3
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
          entity {:education "Postgraduate Education",
                  :work-experience "Senior",
                  :technical-skills "Advanced",
                  :soft-skills "Medium",
                  :references "No",
                  :communication-skills "Good",
                  :problem-solving-ability "High",
                  :cultural-fit "High Fit",
                  :learning-ability "Medium"}]
      (is (= (tree-predict tree entity) "High Fit")))))

(tree-test-3)

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
      tree (create-tree training-data :otplata [:zaduzenje :primanja :stan])]
  (print-tree tree 0))


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

;***********************************************************
;                 NORMALIZACIJA - TESTOVI
;***********************************************************\

(deftest sum-of-ratings-test
  (testing "Testing function that summarize all of ratings for candidates"
    (let [ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]]
      (is (= (sum-of-all-ratings 1 1 ratings-sim) 18)))))

(sum-of-ratings-test)


(deftest sum-of-ratings-test-2
  (testing "Testing function that summarize all of ratings for candidates"
    (let [ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]]
      (is (= (sum-of-all-ratings 1 2 ratings-sim) 19.1)))))

(sum-of-ratings-test-2)

(deftest sum-of-ratings-test-3
  (testing "Testing function that summarize all of ratings for candidates"
    (let [ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]]
      (is (= (sum-of-all-ratings 1 3 ratings-sim) 17)))))

(sum-of-ratings-test-3)


(deftest normalized-ratings-test
  (testing "Testing get-normalized-ratings-of-criteria function"
    (let [ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]]
      (is (= (get-normalized-ratings-of-criteria 1 1 ratings-sim) [{:id 1, :candidate-id 1, :job-id 1, :qualification-id 1, :value 10, :normalized-value 0.5555555555555556}
                                                                   {:id 4, :candidate-id 2, :job-id 1, :qualification-id 1, :value 8, :normalized-value 0.4444444444444444}])))))

(deftest normalized-ratings-test-2
  (testing "Testing get-normalized-ratings-of-criteria function"
    (let [ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]]
      (is (= (get-normalized-ratings-of-criteria 1 2 ratings-sim) [{:id 2, :candidate-id 1, :job-id 1, :qualification-id 2, :value 10, :normalized-value 0.5235602094240838}
                                                                   {:id 5, :candidate-id 2, :job-id 1, :qualification-id 2, :value 9.1, :normalized-value 0.4764397905759162}])))))

(deftest normalized-ratings-test-3
  (testing "Testing get-normalized-ratings-of-criteria function"
    (let [ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]]
      (is (= (get-normalized-ratings-of-criteria 1 3 ratings-sim) [{:id 3, :candidate-id 1, :job-id 1, :qualification-id 3, :value 10, :normalized-value 0.5882352941176471}
                                                                   {:id 6, :candidate-id 2, :job-id 1, :qualification-id 3, :value 7, :normalized-value 0.4117647058823529}])))))

(normalized-ratings-test)
(normalized-ratings-test-2)
(normalized-ratings-test-3)

(deftest normalize-job-ratings-test
  (testing "Testing normalize-job-ratings function"
    (let [ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]]
      (is (= (normalize-job-ratings 1 ratings-sim) `({:id 1, :candidate-id 1, :job-id 1, :qualification-id 1, :value 10, :normalized-value 0.5555555555555556}
                                                     {:id 2, :candidate-id 1, :job-id 1, :qualification-id 2, :value 10, :normalized-value 0.5235602094240838}
                                                     {:id 3, :candidate-id 1, :job-id 1, :qualification-id 3, :value 10, :normalized-value 0.5882352941176471}
                                                     {:id 4, :candidate-id 2, :job-id 1, :qualification-id 1, :value 8, :normalized-value 0.4444444444444444}
                                                     {:id 5, :candidate-id 2, :job-id 1, :qualification-id 2, :value 9.1, :normalized-value 0.4764397905759162}
                                                     {:id 6, :candidate-id 2, :job-id 1, :qualification-id 3, :value 7, :normalized-value 0.4117647058823529}))))))

(normalize-job-ratings-test)


;***********************************************************
;               AGREGACIJA OCENA - TESTOVI
;***********************************************************

(deftest add-ponder-to-normalized-ratings-test
  (testing "Testing add-ponder-to-normalized-ratings function"
    (let [ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]
          criteria-sim [{:job-id 1
                         :qualification-id 1
                         :ponder 0.5},
                        {:job-id 1
                         :qualification-id 2
                         :ponder 0.3},
                        {:job-id 1
                         :qualification-id 3
                         :ponder 0.2}]]
      (is (= (add-ponder-to-normalized-ratings 1 ratings-sim criteria-sim) [{:id 1, :candidate-id 1, :job-id 1, :qualification-id 1, :value 10, :normalized-value 0.5555555555555556, :ponder 0.5}
                                                                            {:id 2, :candidate-id 1, :job-id 1, :qualification-id 2, :value 10, :normalized-value 0.5235602094240838, :ponder 0.3}
                                                                            {:id 3, :candidate-id 1, :job-id 1, :qualification-id 3, :value 10, :normalized-value 0.5882352941176471, :ponder 0.2}
                                                                            {:id 4, :candidate-id 2, :job-id 1, :qualification-id 1, :value 8, :normalized-value 0.4444444444444444, :ponder 0.5}
                                                                            {:id 5, :candidate-id 2, :job-id 1, :qualification-id 2, :value 9.1, :normalized-value 0.4764397905759162, :ponder 0.3}
                                                                            {:id 6, :candidate-id 2, :job-id 1, :qualification-id 3, :value 7, :normalized-value 0.4117647058823529, :ponder 0.2}])))))

(add-ponder-to-normalized-ratings-test)


(deftest aggregate-candidate-test
  (testing "Testing aggregate-candidate function"
    (let [candidates-sim [{:id 1
                           :firstname "mario"
                           :lastname "tavic"
                           :active true
                           :email "dusantavic1@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 2
                           :firstname "nenad"
                           :lastname "panovic"
                           :active true
                           :email "nenadpann@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 3
                           :firstname "arsenije"
                           :lastname "pavlovic"
                           :active true
                           :email "arseenijee00@gmail.com"
                           :status "unrated"
                           :job-id 1}]
          ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]
          criteria-sim [{:job-id 1
                         :qualification-id 1
                         :ponder 0.5},
                        {:job-id 1
                         :qualification-id 2
                         :ponder 0.3},
                        {:job-id 1
                         :qualification-id 3
                         :ponder 0.2}]]
      (is (= (aggregate-candidate 1 candidates-sim ratings-sim criteria-sim) {:id 1,
                                                                              :firstname "mario",
                                                                              :lastname "tavic",
                                                                              :active true,
                                                                              :email "dusantavic1@gmail.com",
                                                                              :status "rated",
                                                                              :job-id 1,
                                                                              :final-score 0.5524928994285323})))))


(deftest aggregate-candidate-test-2
  (testing "Testing aggregate-candidate function"
    (let [candidates-sim [{:id 1
                           :firstname "mario"
                           :lastname "tavic"
                           :active true
                           :email "dusantavic1@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 2
                           :firstname "nenad"
                           :lastname "panovic"
                           :active true
                           :email "nenadpann@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 3
                           :firstname "arsenije"
                           :lastname "pavlovic"
                           :active true
                           :email "arseenijee00@gmail.com"
                           :status "unrated"
                           :job-id 1}]
          ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},

                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7}]
          criteria-sim [{:job-id 1
                         :qualification-id 1
                         :ponder 0.5},
                        {:job-id 1
                         :qualification-id 2
                         :ponder 0.3},
                        {:job-id 1
                         :qualification-id 3
                         :ponder 0.2}]]
      (is (= (aggregate-candidate 2 candidates-sim ratings-sim criteria-sim) {:id 2,
                                                                              :firstname "nenad",
                                                                              :lastname "panovic",
                                                                              :active true,
                                                                              :email "nenadpann@gmail.com",
                                                                              :status "rated",
                                                                              :job-id 1,
                                                                              :final-score 0.44750710057146764})))))

(aggregate-candidate-test)
(aggregate-candidate-test-2)


;***********************************************************
;            PODRSKA U ODLUCIVANJU - TESTOVI
;***********************************************************

(deftest decision-support-test
  (testing "Testing decision-support function"
    (let [candidates-sim [{:id 1
                           :firstname "Dusan"
                           :lastname "Tavic"
                           :active true
                           :email "dusantavic1@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 2
                           :firstname "Nenad"
                           :lastname "Milosevic"
                           :active true
                           :email "nenadmiloss@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 3
                           :firstname "Milos"
                           :lastname "Milovanovic"
                           :active true
                           :email "milosmm@gmail.com"
                           :status "unrated"
                           :job-id 1}]
          ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},
                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7},
                       {:id 7
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 1
                        :value 8.7},
                       {:id 8
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 2
                        :value 7.8},
                       {:id 9
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 3
                        :value 9.5}]
          criteria-sim [{:job-id 1
                         :qualification-id 1
                         :ponder 0.5},
                        {:job-id 1
                         :qualification-id 2
                         :ponder 0.3},
                        {:job-id 1
                         :qualification-id 3
                         :ponder 0.2}]]
      (is (= (decision-support 1 candidates-sim ratings-sim criteria-sim) [{:id 1,
                                                                            :firstname "Dusan",
                                                                            :lastname "Tavic",
                                                                            :active true,
                                                                            :email "dusantavic1@gmail.com",
                                                                            :status "rated",
                                                                            :job-id 1,
                                                                            :final-score 0.37426177928497706}
                                                                           {:id 3,
                                                                            :firstname "Milos",
                                                                            :lastname "Milovanovic",
                                                                            :active true,
                                                                            :email "milosmm@gmail.com",
                                                                            :status "unrated",
                                                                            :job-id 1,
                                                                            :final-score 0.321608309105797}
                                                                           {:id 2,
                                                                            :firstname "Nenad",
                                                                            :lastname "Milosevic",
                                                                            :active true,
                                                                            :email "nenadmiloss@gmail.com",
                                                                            :status "rated",
                                                                            :job-id 1,
                                                                            :final-score 0.3041299116092259}])))))
(decision-support-test)

(let  [candidates-sim [{:id 1
                        :firstname "Marko"
                        :lastname "Radovic"
                        :active true
                        :email "marko@gmail.com"
                        :status "rated"
                        :job-id 1},
                       {:id 2
                        :firstname "Dragana"
                        :lastname "Mirkovic"
                        :active true
                        :email "gaga@gmail.com"
                        :status "rated"
                        :job-id 1},
                       {:id 3
                        :firstname "Maja"
                        :lastname "Petrovic"
                        :active true
                        :email "mayapetrovic@gmail.com"
                        :status "unrated"
                        :job-id 1}]
       ratings-sim [{:id 1
                     :candidate-id 1
                     :job-id 1
                     :qualification-id 1
                     :value 7},
                    {:id 2
                     :candidate-id 1
                     :job-id 1
                     :qualification-id 2
                     :value 8.1},
                    {:id 3
                     :candidate-id 1
                     :job-id 1
                     :qualification-id 3
                     :value 7.8},
                    {:id 4
                     :candidate-id 2
                     :job-id 1
                     :qualification-id 1
                     :value 9.5},
                    {:id 5
                     :candidate-id 2
                     :job-id 1
                     :qualification-id 2
                     :value 10},
                    {:id 6
                     :candidate-id 2
                     :job-id 1
                     :qualification-id 3
                     :value 6.1},
                    {:id 7
                     :candidate-id 3
                     :job-id 1
                     :qualification-id 1
                     :value 7.8},
                    {:id 8
                     :candidate-id 3
                     :job-id 1
                     :qualification-id 2
                     :value 7.9},
                    {:id 9
                     :candidate-id 3
                     :job-id 1
                     :qualification-id 3
                     :value 9.5}]
       criteria-sim [{:job-id 1
                      :qualification-id 1
                      :ponder 0.5},
                     {:job-id 1
                      :qualification-id 2
                      :ponder 0.3},
                     {:job-id 1
                      :qualification-id 3
                      :ponder 0.2}]]
  (decision-support 1 candidates-sim ratings-sim criteria-sim))

(deftest decision-support-test-2
  (testing "Testing decision-support function"
    (let  [candidates-sim [{:id 1
                            :firstname "Marko"
                            :lastname "Radovic"
                            :active true
                            :email "marko@gmail.com"
                            :status "rated"
                            :job-id 1},
                           {:id 2
                            :firstname "Dragana"
                            :lastname "Mirkovic"
                            :active true
                            :email "gaga@gmail.com"
                            :status "rated"
                            :job-id 1},
                           {:id 3
                            :firstname "Maja"
                            :lastname "Petrovic"
                            :active true
                            :email "mayapetrovic@gmail.com"
                            :status "unrated"
                            :job-id 1}]
           ratings-sim [{:id 1
                         :candidate-id 1
                         :job-id 1
                         :qualification-id 1
                         :value 7},
                        {:id 2
                         :candidate-id 1
                         :job-id 1
                         :qualification-id 2
                         :value 8.1},
                        {:id 3
                         :candidate-id 1
                         :job-id 1
                         :qualification-id 3
                         :value 7.8},
                        {:id 4
                         :candidate-id 2
                         :job-id 1
                         :qualification-id 1
                         :value 9.5},
                        {:id 5
                         :candidate-id 2
                         :job-id 1
                         :qualification-id 2
                         :value 10},
                        {:id 6
                         :candidate-id 2
                         :job-id 1
                         :qualification-id 3
                         :value 6.1},
                        {:id 7
                         :candidate-id 3
                         :job-id 1
                         :qualification-id 1
                         :value 7.8},
                        {:id 8
                         :candidate-id 3
                         :job-id 1
                         :qualification-id 2
                         :value 7.9},
                        {:id 9
                         :candidate-id 3
                         :job-id 1
                         :qualification-id 3
                         :value 9.5}]
           criteria-sim [{:job-id 1
                          :qualification-id 1
                          :ponder 0.5},
                         {:job-id 1
                          :qualification-id 2
                          :ponder 0.3},
                         {:job-id 1
                          :qualification-id 3
                          :ponder 0.2}]]
      (is (= (decision-support 1 candidates-sim ratings-sim criteria-sim) [{:id 2,
                                                                            :firstname "Dragana",
                                                                            :lastname "Mirkovic",
                                                                            :active true,
                                                                            :email "gaga@gmail.com",
                                                                            :status "rated",
                                                                            :job-id 1,
                                                                            :final-score 0.3629946185501741}
                                                                           {:id 3,
                                                                            :firstname "Maja",
                                                                            :lastname "Petrovic",
                                                                            :active true,
                                                                            :email "mayapetrovic@gmail.com",
                                                                            :status "unrated",
                                                                            :job-id 1,
                                                                            :final-score 0.33284425451092114}
                                                                           {:id 1,
                                                                            :firstname "Marko",
                                                                            :lastname "Radovic",
                                                                            :active true,
                                                                            :email "marko@gmail.com",
                                                                            :status "rated",
                                                                            :job-id 1,
                                                                            :final-score 0.30416112693890474}])))))


(decision-support-test-2)

(deftest decision-support-test-3
  (testing "Testing decision-support function"
    (let  [candidates-sim [{:id 1
                            :firstname "Marija"
                            :lastname "Obrenovic"
                            :active true
                            :email "marijaobrenovic@gmail.com"
                            :status "rated"
                            :job-id 1},
                           {:id 2
                            :firstname "Leo"
                            :lastname "Tavic"
                            :active true
                            :email "leo.tavic@gmail.com"
                            :status "rated"
                            :job-id 1},
                           {:id 3
                            :firstname "Tea"
                            :lastname "Marjanovic"
                            :active true
                            :email "marjanovictea@gmail.com"
                            :status "unrated"
                            :job-id 1}]
           ratings-sim [{:id 1
                         :candidate-id 1
                         :job-id 1
                         :qualification-id 1
                         :value 6.8},
                        {:id 2
                         :candidate-id 1
                         :job-id 1
                         :qualification-id 2
                         :value 5.6},
                        {:id 3
                         :candidate-id 1
                         :job-id 1
                         :qualification-id 3
                         :value 7.7},
                        {:id 4
                         :candidate-id 2
                         :job-id 1
                         :qualification-id 1
                         :value 9.5},
                        {:id 5
                         :candidate-id 2
                         :job-id 1
                         :qualification-id 2
                         :value 9.7},
                        {:id 6
                         :candidate-id 2
                         :job-id 1
                         :qualification-id 3
                         :value 9.9},
                        {:id 7
                         :candidate-id 3
                         :job-id 1
                         :qualification-id 1
                         :value 7.3},
                        {:id 8
                         :candidate-id 3
                         :job-id 1
                         :qualification-id 2
                         :value 6.0},
                        {:id 9
                         :candidate-id 3
                         :job-id 1
                         :qualification-id 3
                         :value 6.1}]
           criteria-sim [{:job-id 1
                          :qualification-id 1
                          :ponder 0.5},
                         {:job-id 1
                          :qualification-id 2
                          :ponder 0.3},
                         {:job-id 1
                          :qualification-id 3
                          :ponder 0.2}]]
      (is (= (decision-support 1 candidates-sim ratings-sim criteria-sim) [{:id 2,
                                                                            :firstname "Leo",
                                                                            :lastname "Tavic",
                                                                            :active true,
                                                                            :email "leo.tavic@gmail.com",
                                                                            :status "rated",
                                                                            :job-id 1,
                                                                            :final-score 0.4214352085480055}
                                                                           {:id 3,
                                                                            :firstname "Tea",
                                                                            :lastname "Marjanovic",
                                                                            :active true,
                                                                            :email "marjanovictea@gmail.com",
                                                                            :status "unrated",
                                                                            :job-id 1,
                                                                            :final-score 0.2906448524516188}
                                                                           {:id 1,
                                                                            :firstname "Marija",
                                                                            :lastname "Obrenovic",
                                                                            :active true,
                                                                            :email "marijaobrenovic@gmail.com",
                                                                            :status "rated",
                                                                            :job-id 1,
                                                                            :final-score 0.2879199390003757}])))))

(decision-support-test-3)

(deftest selection-advice-test
  (testing "Testing selection-advice function"
    (let [candidates-sim [{:id 1
                           :firstname "Dusan"
                           :lastname "Tavic"
                           :active true
                           :email "dusantavic1@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 2
                           :firstname "Nenad"
                           :lastname "Milosevic"
                           :active true
                           :email "nenadmiloss@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 3
                           :firstname "Milos"
                           :lastname "Milovanovic"
                           :active true
                           :email "milosmm@gmail.com"
                           :status "unrated"
                           :job-id 1}]
          ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 10},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 10},
                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 8},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 9.1},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 7},
                       {:id 7
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 1
                        :value 8.7},
                       {:id 8
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 2
                        :value 7.8},
                       {:id 9
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 3
                        :value 9.5}]
          criteria-sim [{:job-id 1
                         :qualification-id 1
                         :ponder 0.5},
                        {:job-id 1
                         :qualification-id 2
                         :ponder 0.3},
                        {:job-id 1
                         :qualification-id 3
                         :ponder 0.2}]
          jobs-sim [{:id 1
                     :name "C# Junior Developer"
                     :active true
                     :positions 1}]]
      (is (= (selection-advice 1 candidates-sim ratings-sim criteria-sim jobs-sim) ["Top rated Candidate for C# Junior Developer: Dusan Tavic with final score of 0.3743"
                                                                                    "You should also consider Milos Milovanovic with final score of 0.3216 for C# Junior Developer"])))))

(selection-advice-test)

(deftest selection-advice-test-2
  (testing "Testing selection-advice function"
    (let [candidates-sim [{:id 1
                           :firstname "Marko"
                           :lastname "Radovic"
                           :active true
                           :email "marko@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 2
                           :firstname "Dragana"
                           :lastname "Mirkovic"
                           :active true
                           :email "gaga@gmail.com"
                           :status "rated"
                           :job-id 1},
                          {:id 3
                           :firstname "Maja"
                           :lastname "Petrovic"
                           :active true
                           :email "mayapetrovic@gmail.com"
                           :status "unrated"
                           :job-id 1}]
          ratings-sim [{:id 1
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 1
                        :value 7},
                       {:id 2
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 2
                        :value 8.1},
                       {:id 3
                        :candidate-id 1
                        :job-id 1
                        :qualification-id 3
                        :value 7.8},
                       {:id 4
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 1
                        :value 9.5},
                       {:id 5
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 2
                        :value 10},
                       {:id 6
                        :candidate-id 2
                        :job-id 1
                        :qualification-id 3
                        :value 6.1},
                       {:id 7
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 1
                        :value 7.8},
                       {:id 8
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 2
                        :value 7.9},
                       {:id 9
                        :candidate-id 3
                        :job-id 1
                        :qualification-id 3
                        :value 9.5}]
          criteria-sim [{:job-id 1
                         :qualification-id 1
                         :ponder 0.5},
                        {:job-id 1
                         :qualification-id 2
                         :ponder 0.3},
                        {:job-id 1
                         :qualification-id 3
                         :ponder 0.2}]
          jobs-sim [{:id 1
                     :name "C# Junior Developer"
                     :active true
                     :positions 1}]]
      (is (= (selection-advice 1 candidates-sim ratings-sim criteria-sim jobs-sim) ["Top rated Candidate for C# Junior Developer: Dragana Mirkovic with final score of 0.363"
                                                                                    "You should also consider Maja Petrovic with final score of 0.3328 for C# Junior Developer"])))))

(selection-advice-test-2)

;***********************************************************
;                     AHP - TESTOVI
;***********************************************************

(deftest inverse-ponders-test
  (testing "Testing inverse-ponders function"
    (let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 3},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 2},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 0.5}]
          expected [{:job-id 1, :qualification-id-base 1, :qualification-id-reference 2, :position [1 0], :significance 1/3}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 3, :position [2 0], :significance 1/2}
                    {:job-id 1, :qualification-id-base 2, :qualification-id-reference 3, :position [2 1], :significance 2.0}]]
      (is (= (inverse-ponders ahp-ponders) expected)))))

(inverse-ponders-test)

(deftest inverse-ponders-test-2
  (testing "Testing inverse-ponders function"
    (let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 6},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 8},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 1/8}]
          expected [{:job-id 1, :qualification-id-base 1, :qualification-id-reference 2, :position [1 0], :significance 1/6}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 3, :position [2 0], :significance 1/8}
                    {:job-id 1, :qualification-id-base 2, :qualification-id-reference 3, :position [2 1], :significance 8}]]
      (is (= (inverse-ponders ahp-ponders) expected)))))

(inverse-ponders-test-2)

(deftest add-inverse-ponders-test
  (testing "Testing add-inverse-ponders function"
    (let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 3},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 2},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 0.5}]
          expected [{:job-id 1, :qualification-id-base 1, :qualification-id-reference 2, :position [0 1], :significance 3}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 3, :position [0 2], :significance 2}
                    {:job-id 1, :qualification-id-base 2, :qualification-id-reference 3, :position [1 2], :significance 0.5}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 2, :position [1 0], :significance 1/3}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 3, :position [2 0], :significance 1/2}
                    {:job-id 1, :qualification-id-base 2, :qualification-id-reference 3, :position [2 1], :significance 2.0}]]
      (is (= (add-inverse-ponders ahp-ponders) expected)))))

(add-inverse-ponders-test)

(deftest add-inverse-ponders-test-2
  (testing "Testing add-inverse-ponders function"
    (let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 6},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 8},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 0.5}]
          expected [{:job-id 1, :qualification-id-base 1, :qualification-id-reference 2, :position [0 1], :significance 6}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 3, :position [0 2], :significance 8}
                    {:job-id 1, :qualification-id-base 2, :qualification-id-reference 3, :position [1 2], :significance 0.5}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 2, :position [1 0], :significance 1/6}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 3, :position [2 0], :significance 1/8}
                    {:job-id 1, :qualification-id-base 2, :qualification-id-reference 3, :position [2 1], :significance 2.0}]]
      (is (= (add-inverse-ponders ahp-ponders) expected)))))

(add-inverse-ponders-test-2)

(deftest get-ponders-test
  (testing "Testing get-ahp-ponders function"
    (let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 3},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 2},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 0.5}]
          expected [{:job-id 1, :qualification-id-base 1, :qualification-id-reference 2, :position [0 1], :significance 3}
                    {:job-id 1, :qualification-id-base 1, :qualification-id-reference 3, :position [0 2], :significance 2}
                    {:job-id 1, :qualification-id-base 2, :qualification-id-reference 3, :position [1 2], :significance 0.5}]]
      (is (= (get-ahp-ponders 1 ahp-ponders) expected)))))

(get-ponders-test)

(deftest create-matrix-test
  (testing "Testing create-matrix function"
    (is (= (create-matrix 3) [[1 1 1] [1 1 1] [1 1 1]]))))

(deftest create-matrix-test-2
  (testing "Testing create-matrix function"
    (is (= (create-matrix 4 7) [[1 1 1 1 1 1 1] [1 1 1 1 1 1 1] [1 1 1 1 1 1 1] [1 1 1 1 1 1 1]]))))

(create-matrix-test)
(create-matrix-test-2)

(deftest create-ahp-matrix-test
  (testing "Testing create-ahp-matrix function"
    (let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 3},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 2},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 0.5}]]
      (is (= (create-ahp-matrix 1 ahp-ponders) [[1 3 2] [1/3 1 0.5] [1/2 2.0 1]])))))

(create-ahp-matrix-test)

(deftest calculate-total-array-test
  (testing "Testing calculate-total-array function"
    (let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 3},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 2},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 0.5}]]
      (is (= (calculate-total-array 1 ahp-ponders) [0.5294117647058824 0.16176470588235292 0.3088235294117647])))))


(calculate-total-array-test)

(deftest calculate-ahp-test
  (testing "Testing calculate-ahp function"
    (let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 3},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 2},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 0.5}]]
      (is (= (calculate-ahp 1 ahp-ponders) [{:job-id 1, :qualification-id 1, :ponder 0.5, :ahp-ponder 0.5294117647058824}
                                            {:job-id 1, :qualification-id 2, :ponder 0.3, :ahp-ponder 0.16176470588235292}
                                            {:job-id 1, :qualification-id 3, :ponder 0.2, :ahp-ponder 0.3088235294117647}])))))

(let [ahp-ponders [{:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 2
                        :position [0,1]
                        :significance 3},
                       {:job-id 1
                        :qualification-id-base 1
                        :qualification-id-reference 3
                        :position [0,2]
                        :significance 2},
                       {:job-id 1
                        :qualification-id-base 2
                        :qualification-id-reference 3
                        :position [1,2]
                        :significance 0.5}]]
      (calculate-ahp 1 ahp-ponders))

(calculate-ahp-test)
