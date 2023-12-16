(ns clojureselect.data
  (:gen-class))


;***********************************************************
;            ALATI I METODE VESTACKE INTELIGENCIJE         *
;                I SOFTVERSKOG INZENJERSTVA                *
;                          2023.                           *
;             --------------------------------             *
;                      Clojure Select                      *
;              ~Decision Support Clojure App~              *
;             --------------------------------             *
;                        Dusan Tavic                       *
;                         2023/3801                        *
;***********************************************************


;***********************************************************
;                 SIMULACIJA PODATAKA U BAZI 
;***********************************************************

(def candidates [{:id 1
                  :firstname "dusan"
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
                  :job-id 1}])

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
                :ponder 0.2}])

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
               :value 7}])


;***********************************************************
;                     OSNOVNE FUNKCIJE 
;***********************************************************

(defn get-candidate [candidate-id]
  (into {} (filter (fn [candidate] (= (:id candidate) candidate-id)) candidates)))

(defn get-ratings
  [candidate-id]
  (into [] (filter (fn [rating] (= (:candidate-id rating) candidate-id)) ratings)))

(get-ratings 1)


(defn get-candidates
  [job-id]
  (into [] (filter (fn [candidate] (= (:job-id candidate) job-id)) candidates)))

(get-candidates 1)

(defn get-jobs-criteria [job-id]
  (into [] (filter (fn [criteria] (= (:job-id criteria) job-id)) criteria)))

(get-jobs-criteria 1)

(defn get-ponder
  "Returns a ponder value (criteria weight) for a specific criteria"
  [job-id qualification-id]
  (double (:ponder (into {} (filter (fn [criteria] (and (= (:job-id criteria) job-id) (= (:qualification-id criteria) qualification-id))) criteria)))))

(get-ponder 1 1)

;criteria-id = job-id + qualification-id
(defn get-ratings-of-criteria
  "Gets ratings of all candidates for a specific criteria"
  [job-id qualification-id]
  (into [] (filter (fn [rating] (and (= (:job-id rating) job-id) (= (:qualification-id rating) qualification-id))) ratings)))

(get-ratings-of-criteria 1 1)


(defn get-ratings-of-job
  "Gets ratings of all candidates according to all criteria for a specific job"
  [job-id]
  (filter (fn [rating] (= (:job-id rating) job-id)) ratings))

(get-ratings-of-job 1)


;***********************************************************
;                     NORMALIZACIJA 
;***********************************************************

(defn sum-of-all-ratings
  "Calculates sum of ratings of all candidates who have applied for a specific job according to a specific criteria"
  [job-id qualification-id]
  (reduce + (map :value (get-ratings-of-criteria job-id qualification-id))))

(sum-of-all-ratings 1 1)
(sum-of-all-ratings 1 2)
(sum-of-all-ratings 1 3)

(defn get-normalized-ratings-of-criteria
  "Calculates normalized values of ratings according to a specific criteria"
  [job-id qualification-id]
  (into [] (map (fn [row] (assoc row :normalized-value (/ (double (:value row)) (sum-of-all-ratings job-id qualification-id)))) (get-ratings-of-criteria job-id qualification-id))))

(get-normalized-ratings-of-criteria 1 1)
(get-normalized-ratings-of-criteria 1 2)
(get-normalized-ratings-of-criteria 1 3)

(defn get-normalized-rating
  "Returns a rating with added field :normalized-value"
  [rating]
  (into {} (assoc rating :normalized-value (/ (double (:value rating)) (sum-of-all-ratings (:job-id rating) (:qualification-id rating))))))


(defn normalize-job-ratings [job-id]
  "Normalizes all ratings of all candidates for a specific job"
  (let [ratings (get-ratings-of-job job-id)]
    (map (fn [row] (get-normalized-rating row)) ratings)))

(normalize-job-ratings 1)


;***********************************************************
;                     AGREGACIJA OCENA 
;***********************************************************

(defn add-ponder-to-normalized-ratings
  [job-id]
  (let [normalized-ratings (normalize-job-ratings job-id)]
    (into [] (map (fn [row] (assoc row :ponder (get-ponder job-id (:qualification-id row)))) normalized-ratings))))

(add-ponder-to-normalized-ratings 1)

;; (map (fn [row] (* (:normalized-value row) (:ponder row))) (filter (fn [rating] (= (:candidate-id rating) 1)) (add-ponder-to-normalized-ratings 1)))


(defn aggregate-candidate [candidate-id]
  (let [candidate (get-candidate candidate-id)
        ratings (add-ponder-to-normalized-ratings (:job-id candidate))]
    (let [candidates-ratings (filter (fn [rating] (= (:candidate-id rating) candidate-id)) ratings)]
      (assoc candidate :final-score (double (reduce + (map (fn [row] (* (:normalized-value row) (:ponder row))) (filter (fn [rating] (= (:candidate-id rating) candidate-id)) ratings))))))))

(aggregate-candidate 1)
(aggregate-candidate 2)


;***********************************************************
;                   PODRSKA U ODLUCIVANJU 
;***********************************************************


(defn decision-support
  "Applies the method of multi-criteria decision-making and provides
   advices for the most suitable candidates for a specific job"
  [job-id]
  (let [candidates (get-candidates job-id)]
    (into [] (sort-by :final-score (comparator >) (map (fn [row] (aggregate-candidate (:id row))) candidates)))))

(decision-support 1)


;***********************************************************
;                 NEKI DELOVI STAROG KODA 
;***********************************************************

;; (defn summarize-ratings [candidate-id]
;;  (reduce + (map (fn [row] (:value row)) (get-ratings candidate-id)))) ;daje sumu svih ocena za jednog kandidata

;; (defn summarize-raings1 [candidate-id]
;;   (map (fn [row] (assoc row :normalized-value (/ (double (:value row)) (summarize-ratings candidate-id)))) (get-ratings candidate-id)))
;OVO JE NORMALIZACIJA! --- nije dobro!!!!!
;NE RADI SE SABIRANJE PO SVIM KRITERIJUMIMA ZA JEDNOG KANDIDATA
;NEGO SABIRANJE JEDNOG KRITERIJUMA ZA SVE KANDIDATE!!! 


;; (summarize-raings1 2)

;; (defn summarize-ratings2 [candidate-id] 
;;   (let [candidate (get-candidate candidate-id)]
;;     (assoc candidate :final-score (summarize-ratings candidate-id))))

;; (defn summarize-ratings3 []
;;   (map (fn [row] (summarize-ratings2 (:id row))) candidates)) ;radi za sve kandidate!

;; (summarize-ratings3)
;; (summarize-ratings2 1)

;; (summarize-ratings 1)
;; (summarize-ratings 2)
;; (summarize-ratings 3)



;radi otezanu sumu za jednog kandidata
;; (defn weighted-sum [candidate-id]
;;   (let [candidate (get-candidate candidate-id)]
;;     (let [criteria (get-jobs-criteria (:job-id candidate))
;;           normalized-candidate (summarize-raings1 candidate-id)]
;;       (map (fn [row] (assoc row :weighted-sum (* (:normalized-value row) (get-ponder (:job-id row) (:qualification-id row)))))
;;            normalized-candidate )
;;       )))


;; (weighted-sum 1)

;; (defn aggregated-score [candidate-id]
;;   (let [weighted-candidate (weighted-sum candidate-id)]
;;     (reduce + (map :weighted-sum weighted-candidate))))

;; (aggregated-score 1)
;; (aggregated-score 2)


;; (def niz1 [1 2 3])
;; (def niz2 [3 4 5])
;; ;rezultat - [3 8 15]

;; (into [] (map * niz1 niz2))

(def maps [{:id 1 :number 1}
           {:id 2 :number 100}
           {:id 3 :number 200}])

(map :number maps)
