(ns clojureselect.business-logic
  (:require [clojureselect.repository :as repository])
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
;          UCITAVANJE PERZISTENTNIH PODATAKA IZ BAZE
;***********************************************************

(def candidates (repository/get-candidates))
(def jobs (repository/get-jobs))
(def qualifications (repository/get-qualifications))
(def criteria (repository/get-criteria))
(def ratings (repository/get-ratings))

;***********************************************************
;                     OSNOVNE FUNKCIJE 
;***********************************************************

(defn get-candidate 
  "Returns the candidate's object that contains all relevant information, 
   based on the entered id."
  [candidate-id]
  (into {} (filter (fn [candidate] (= (:id candidate) candidate-id)) candidates)))

(defn get-ratings
  "Returns all of the candidate's ratings. 
   Candidates are rated based on selection criteria defined for the job."
  [candidate-id]
  (into [] (filter (fn [rating] (= (:candidate-id rating) candidate-id)) ratings)))

(get-ratings 1)
(get-ratings 2)


(defn get-candidates
  "Returns all candidates who applied for a specific job"
  [job-id]
  (into [] (filter (fn [candidate] (= (:job-id candidate) job-id)) candidates)))

(get-candidates 1)

(defn get-jobs-criteria 
  "Returns all selection criteria for a specific job"
  [job-id]
  (into [] (filter (fn [criteria] (= (:job-id criteria) job-id)) criteria)))

(get-jobs-criteria 1)

(defn get-jobs-name 
  "Returns the job name based on the entered id"
  [job-id]
  (:name (into {} (filter (fn [job] (= (:id job) job-id)) jobs))))

(get-jobs-name 1)

(defn get-ponder
  "Returns a ponder value (criteria weight) for a specific criteria"
  [job-id qualification-id]
  (if (or (<= job-id 0) (<= qualification-id 0))
    (str "error." " please insert valid id values")
    (double (:ponder (into {} (filter (fn [criteria] (and (= (:job-id criteria) job-id) (= (:qualification-id criteria) qualification-id))) criteria))))))

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
  (into [] (filter (fn [rating] (= (:job-id rating) job-id)) ratings)))

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
  "Adds a ponder to the normalized ratings of all candidates who applied for a specific job"
  [job-id]
  (let [normalized-ratings (normalize-job-ratings job-id)]
    (into [] (map (fn [row] (assoc row :ponder (get-ponder job-id (:qualification-id row)))) normalized-ratings))))

(add-ponder-to-normalized-ratings 1)

;; (map (fn [row] (* (:normalized-value row) (:ponder row))) (filter (fn [rating] (= (:candidate-id rating) 1)) (add-ponder-to-normalized-ratings 1)))


(defn aggregate-candidate 
  "Aggregates the overall rating of the candidate by using a weighted sum"
  [candidate-id]
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

(defn round-to-decimal-places [num places]
  (Double/parseDouble (format (str "%." places "f") num)))

(defn selection-advice 
  "Gives advice for the top two most suitable candidates for a specific job"
  [job-id]
  (let [rated-candidates (decision-support job-id)]
    (let [first-candidate (get rated-candidates 0)
          second-candidate (get rated-candidates 1)]
      [(str "Top rated Candidate for " (get-jobs-name job-id) ": " (:firstname first-candidate) " " (:lastname first-candidate) " with final score of " (round-to-decimal-places (:final-score first-candidate) 4))
      (str "You should also consider " (:firstname second-candidate) " " (:lastname second-candidate) " with final score of " (round-to-decimal-places (:final-score second-candidate) 4) " for " (get-jobs-name job-id))])))

(selection-advice 1)


;***********************************************************
;                 SIMULACIJA PODATAKA U BAZI 
;***********************************************************

(defn database-simulation []
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
                 :value 7}]))
