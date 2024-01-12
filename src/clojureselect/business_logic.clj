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
  "Returns all selection criteria for a spec
   ific job"
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

(defn get-qualification
  "Returns a specific qualification"
  [qualification-id]
  (into {} (filter (fn [qual] (= (:id qual) qualification-id)) qualifications)))


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


(defn normalize-job-ratings 
  "Normalizes all ratings of all candidates for a specific job"
  [job-id] 
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
;        AHP - ODREDJIVANJE VAZNOSTI KRITERIJUMA
;***********************************************************
; u okviru ahp-ponders nalaze se preference donosilaca odluka 


(def ahp-ponders [{:job-id 1
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
                   :significance 0.5}]) ;treba da se cuva u bazi


(defn inverse-ponders 
  "Inverts AHP ponders. If significance value on a specific position is x, then significance value on inverted position must be 1/x"
  [raw-ponders]
  (into [] (map (fn [obj] (let [position1 (get (:position obj) 0)
                       position2 (get (:position obj) 1)] (assoc obj :position [position2, position1] :significance (/ 1 (:significance obj))))) raw-ponders)))

(defn add-inverse-ponders 
  "Adds inverted ponders to an array of initially created ponders"
  [raw-ponders]
  (into [] (concat ahp-ponders (inverse-ponders raw-ponders))))

(defn get-ahp-ponders
  "Returns ahp ponders for a specific job"
  [job-id]
  (into [] (filter (fn [row] (= (:job-id row) job-id)) ahp-ponders)))

(defn get-nth-ahp-ponder [job-id n]
  (let [ponders (get-ahp-ponders job-id)]
  (into {} (filter (fn [ponder] (= (:number ponder) n)) ponders))))

(defn create-matrix
  "Creates a matrix with specific number of rows and columns, initially filled with ones"
  ([rows columns]
   (vec (for [x (range rows)]
          (vec (repeat columns 1)))))
  ([rows-and-cols]
   (vec (for [x (range rows-and-cols)]
          (vec (repeat rows-and-cols 1))))))

(defn modify-ahp-matrix
  "Recursive function that modifies values in ahp matrix"
  [matrix ponders]
  (if-not (= (rest ponders) [])
    (let [current-ponder (first ponders)
          mat (modify-ahp-matrix matrix (into [] (rest ponders)))]
      (assoc-in mat [(get (:position current-ponder) 0) (get (:position current-ponder) 1)] (:significance current-ponder)))
    (let [current-ponder (first ponders)]
      (assoc-in matrix [(get (:position current-ponder) 0) (get (:position current-ponder) 1)] (:significance current-ponder)))))

(defn print-matrix 
  "Prints a matrix in a readable format"
  [matrix]
  (doseq [row matrix]
    (println (apply str (interpose "\t" row)))))

(defn create-ahp-matrix
  "Creates AHP matrix of assessments for a specific job"
  [job-id]
  (let [raw-ponders (get-ahp-ponders job-id)
        final-ponders (add-inverse-ponders raw-ponders)
        rows-cols-count (count ahp-ponders)
        init-matrix (create-matrix rows-cols-count)]
    (modify-ahp-matrix init-matrix final-ponders)))


(create-ahp-matrix 1)

(defn calculate-total-array
  "Calculates total array of weights for all criteria using AHP methodology"
  [job-id]
  (let [ahp-matrix (create-ahp-matrix job-id)
        array-sums (into [] (map (fn [arr] (reduce + arr)) ahp-matrix))
        total-sum (reduce + array-sums)]
    (into [] (map (fn [element] (/ element total-sum)) array-sums)))) 

(calculate-total-array 1)

(defn calculate-ahp
  "Calculates total ahp weights for criteria"
  [job-id]
  (let [ahp-array (calculate-total-array job-id)
        criteria (get-jobs-criteria job-id)]
    (into [] (map (fn [element] (assoc element :ahp-ponder (get ahp-array (.indexOf criteria element)))) criteria))
      ))

(calculate-ahp 1)

;***********************************************************
;                    STABLO ODLUCIVANJA
;***********************************************************

(def testpodaci [{:zaduzenje "kriticno"
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


(defn frequencies-map [data attribute]
  (let [values (map attribute data)]
    (frequencies values)))
;keys vals

(defn log2 [x]
  (/ (Math/log x) (Math/log 2)))

;; (defn get-count-yes
;;   "Returns total number of elements with positive value of output variable"
;;   [data out yes-value]
;;   (reduce (fn [acc element]
;;             (if (= (out element) yes-value)
;;               (inc acc)
;;               acc)) 0 data))

;; (defn entropy
;;   [data out yes-value]
;;   (let [total (count data)
;;         count-yes (get-count-yes data out yes-value)
;;         count-no (- total count-yes)
;;         probability-yes (/ count-yes total)
;;         probability-no (/ count-no total)]
;;     (* -1 (+ (* probability-yes (log2 probability-yes))
;;              (* probability-no (log2 probability-no))))))

;; (entropy testpodaci :otplata "da")

(defn entropy 
  "Calcuates entropy of a given column.
   As an input parameter expects only one column for which the entropy needs to be calculated."
  [data]
  (let [value-counts (frequencies data)
        values (keys value-counts)
        counts (vals value-counts)
        total-count (apply + counts)
        probabilities (map #(/ % total-count) counts)]
    (->> (map #(* % (log2 %)) probabilities)
         (reduce +)
         (* -1))))

(defn extract-column
  "Extracts values only for the column of the entered attribute"
  [data attribute]
  (map #(get % attribute) data))

(entropy (extract-column testpodaci :otplata))


;; (def datatest (map #(get % :otplata) testpodaci))
;; (def value-counts (frequencies datatest))
;; (def values (keys value-counts)) 
;; (def counts (vals value-counts)) 
;; (def total-count (apply + counts))
;; (def probabilities (map #(/ % total-count) counts))

;; (->>
;;  (map #(* % (log2 %)) probabilities)
;;  (reduce +)
;;  (* -1))

(defn index-of-max 
  "Returns an index of the greatest number element in an array."
  [arr]
  (let [max-index (apply max-key (fn [i] (nth arr i)) (range (count arr)))]
    max-index))

(defn conditional-entropy 
  [data attribute out values counts]
  (reduce +
          (map (fn [i]
                 (let [value (nth values i)
                       subset (->> data
                                   (filter #(= value (get % attribute))))
                       subset-entropy (entropy (extract-column subset out))]
                   (* (/ (nth counts i) (apply + counts)) subset-entropy)))
               (range (count values)))))


(defn information-gain 
  [data attribute out]
  (let [total-entropy (entropy (extract-column data out))]
    (let [values-counts (frequencies (extract-column data attribute))
          values (keys values-counts)
          counts (vals values-counts)
          conditional-entropy (conditional-entropy data attribute out values counts)]
      (- total-entropy conditional-entropy))))


(information-gain testpodaci :stan :otplata)

(defn best-attribute
  [data out attributes]
  (let [information-gains (map #(information-gain data % out) attributes)
        best-attribute-index (index-of-max information-gains)
        best-attribute (nth attributes best-attribute-index)]
    best-attribute))


(best-attribute testpodaci :otplata [:zaduzenje :primanja :stan])

(require 'clojure.tools.trace)

(defn id3 [data out attributes]
  (if (= 1 (count (distinct (extract-column data out))))
    (first (distinct (extract-column data out)))
    (if (empty? attributes)
      (let [unique-target-values (map #(get % out) data)
            counts (frequencies unique-target-values)
            majority-class (->> counts
                                (apply max-key second)
                                first)]
        majority-class)
      (let [best-attribute (best-attribute data out attributes) 
            attributes (remove #(= % best-attribute) attributes)]
        (def tree {best-attribute {}})
        (doseq [value (distinct (map #(get % best-attribute) data))]
          (let [sub-data (filter #(= value (get % best-attribute)) data)
                subtree (id3 sub-data out attributes)]
            (def tree (assoc-in tree [best-attribute value] subtree)))
          tree
            )
        tree
          ))))


(defn id3-nodef [data out attributes]
  (if (= 1 (count (distinct (extract-column data out))))
    (first (distinct (extract-column data out)))
    (if (empty? attributes)
      (let [unique-target-values (map #(get % out) data)
            counts (frequencies unique-target-values)
            majority-class (->> counts
                                (apply max-key second)
                                first)]
        majority-class)
      (let [best-attribute (best-attribute data out attributes)
            attributes (remove #(= % best-attribute) attributes)]
        (->> (distinct (map #(get % best-attribute) data))
             (reduce (fn [tree value]
                       (let [sub-data (filter #(= value (get % best-attribute)) data)
                             subtree (id3 sub-data out attributes)]
                         (assoc-in tree [best-attribute value] subtree)))
                     {best-attribute {}}))))))


(def attributes [:zaduzenje :primanja :stan])
(id3 testpodaci :otplata [:zaduzenje :primanja :stan])
(id3-nodef testpodaci :otplata [:zaduzenje :primanja :stan])
(clojure.tools.trace/dotrace [id3] (id3 testpodaci :otplata [:zaduzenje :primanja :stan]))

;; {:zaduzenje
;;  {"kriticno" "ne",
;;   "prihvatljivo" {:stan {"da" "da", "ne" "ne"}},
;;   "povoljno" {:primanja {"niska" "ne"}, :stan {"ne" {:primanja {"niska" "ne"}}}}}}



;***********************************************************
;                 SIMULACIJA PODATAKA U BAZI 
;***********************************************************
; koristi se iskljucivo za potrebe testiranja funkcija

(def candidates-sim [{:id 1
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
                    :job-id 1}])

(def jobs-sim [{:id 1
              :name "C# Junior Developer"
              :active true
              :positions 1}])

(def qualifications-sim [{:id 1
                        :name "C# Test"},
                       {:id 2
                        :name "Education"},
                       {:id 3
                        :name "Abstract thinking"}])

(def criteria-sim [{:job-id 1
                  :qualification-id 1
                  :ponder 0.5},
                 {:job-id 1
                  :qualification-id 2
                  :ponder 0.3},
                 {:job-id 1
                  :qualification-id 3
                  :ponder 0.2}])


(def ratings-sim [{:id 1
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



