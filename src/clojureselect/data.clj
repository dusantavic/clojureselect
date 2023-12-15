(ns clojureselect.data
  (:gen-class))


;objektna simulacija podataka u bazi: 

(def candidates [{:id 1
                  :firstname "dusan"
                  :lastname "tavic"
                  :active true
                  :email "dusantavic1@gmail.com"
                  :candidatestatus "rated"
                  :job-id 1}, 
                 {:id 2
                  :firstname "nenad"
                  :lastname "panovic"
                  :active true
                  :email "nenadpann@gmail.com"
                  :candidatestatus "rated"
                  :job-id 1}, 
                 {:id 3
                  :firstname "arsenije"
                  :lastname "pavlovic"
                  :active true
                  :email "arseenijee00@gmail.com"
                  :candidatestatus "unrated"
                  :job-id 1}, 
                 ])

(def jobs [{:id 1
            :name "C# Junior Developer"
            :active true
            :positions 1}])

(def qualifications [{:id 1
                      :name "C# Test"},
                     {:id 2
                      :name "Education"},
                     {:id 3
                      :name "Abstract thinking"}])

(def criteria [{:job-id 1
                :qualification-id 1
                :ponder 0.5}, 
               {:job-id 1
                :qualification-id 2
                :ponder 0.3},
               {:job-id 1
                :qualification-id 3
                :ponder 0.2}
               ])

(def ratings [{:id 1
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
               :value 7}
              ])

(defn get-candidate [candidate-id]
  (into {} (filter (fn [candidate] (= (:id candidate) candidate-id)) candidates)))

(defn get-ratings 
  [candidate-id]
  (into [] (filter (fn [rating] (= (:candidate-id rating) candidate-id) ) ratings)))

(get-ratings 1)

(defn get-jobs-criteria
  [job-id]
  (into [] (filter (fn [criteria] (= (:job-id criteria) job-id)) criteria)))

(get-jobs-criteria 1)

(defn get-candidates
  [job-id]
  (into [] (filter (fn [candidate] (= (:job-id candidate) job-id)) candidates)))

(get-candidates 1)

(defn get-jobs-criteria [job-id]
  (into [] (filter (fn [criteria] (= (:job-id criteria) job-id)) criteria)))

(get-jobs-criteria 1)

(defn get-ponder [job-id qualification-id]
  (double (:ponder (into {} (filter (fn [criteria] (and (= (:job-id criteria) job-id) (= (:qualification-id criteria) qualification-id))) criteria)))))

(get-ponder 1 1)



;; (defn decision-support 
;;   [job-id]
;;   (let [candidates (get-candidates job-id)]
;;     ))

;; (defn normalization [candidate] 
;;     (let [total 0]
;;       (let [sum (for [i (range (count (get-ratings (:id candidate))))]
;;                 :let [total (+ total (:value (get (get-ratings (:id candidate)) i)))]
;;                 :when (= 2 2))]
;;       (println sum))))


(defn summarize-ratings [candidate-id]
 (reduce + (map (fn [row] (:value row)) (get-ratings candidate-id)))) ;daje sumu svih ocena za jednog kandidata

(defn summarize-raings1 [candidate-id]
  (map (fn [row] (assoc row :normalized-value (/ (double (:value row)) (summarize-ratings candidate-id)))) (get-ratings candidate-id)))


(summarize-raings1 2)

(defn summarize-ratings2 [candidate-id] 
  (let [candidate (get-candidate candidate-id)]
    (assoc candidate :final-score (summarize-ratings candidate-id))))

(defn summarize-ratings3 []
  (map (fn [row] (summarize-ratings2 (:id row))) candidates)) ;radi za sve kandidate!

(summarize-ratings3)
(summarize-ratings2 1)

(summarize-ratings 1)
(summarize-ratings 2)
(summarize-ratings 3)

(def personanongrata {:name "dusan" :age 23})
(assoc personanongrata :name "mariofil")
(def personanongrata (assoc personanongrata :lastname "tavic"))
personanongrata

;radi otezanu sumu za jednog kandidata
(defn weighted-sum [candidate-id]
  (let [candidate (get-candidate candidate-id)]
    (let [criteria (get-jobs-criteria (:job-id candidate))
          normalized-candidate (summarize-raings1 candidate-id)]
      (map (fn [row] (assoc row :weighted-sum (* (:normalized-value row) (get-ponder (:job-id row) (:qualification-id row)))))
           normalized-candidate )
      )))


(weighted-sum 1)

(defn aggregated-score [candidate-id]
  (let [weighted-candidate (weighted-sum candidate-id)]
    (reduce + (map :weighted-sum weighted-candidate))))

(aggregated-score 1)
(aggregated-score 2)


;; (def niz1 [1 2 3])
;; (def niz2 [3 4 5])
;; ;rezultat - [3 8 15]

;; (into [] (map * niz1 niz2))

(def maps [{:id 1 :number 1}
           {:id 2 :number 100}
           {:id 3 :number 200}])

(map :number maps)
