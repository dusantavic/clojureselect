(ns clojureselect.business-logic
  (:require [clojureselect.repository :as repository])
  (:gen-class))

;***********************************************************
;            ALATI I METODE VESTACKE INTELIGENCIJE         *
;                I SOFTVERSKOG INZENJERSTVA                *
;                        2023/2024.                        *
;             --------------------------------             *
;                      Clojure Select                      *
;              ~Decision Support Clojure App~              *
;             --------------------------------             *
;                        Dusan Tavic                       *
;                         2023/3801                        *
;***********************************************************

;***********************************************************
;                    STABLO ODLUCIVANJA
;***********************************************************


(defn frequencies-map [data attribute]
  (let [values (map attribute data)]
    (frequencies values)))
;keys vals

(defn log2 [x]
  (/ (Math/log x) (Math/log 2)))


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


(defn index-of-max
  "Returns an index of the greatest number element in an array."
  [arr]
  (let [max-index (apply max-key (fn [i] (nth arr i)) (range (count arr)))]
    max-index))

(defn conditional-entropy
  "Returns the conditional entropy for a set or subset of data."
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
  "Determines the significance of the input attribute in predicting the output attribute."
  [data attribute out]
  (let [total-entropy (entropy (extract-column data out))]
    (let [values-counts (frequencies (extract-column data attribute))
          values (keys values-counts)
          counts (vals values-counts)
          conditional-entropy (conditional-entropy data attribute out values counts)]
      (- total-entropy conditional-entropy))))


(defn best-attribute
  "Returns the attribute that has the highest significance for making predictions. 
   If no attribute provides supplementary information for prediction returns nil."
  [data out attributes]
  (let [information-gains (map #(information-gain data % out) attributes)
        best-attribute-index (index-of-max information-gains)
        best-attribute (nth attributes best-attribute-index)]
    (if (every? (fn [x] (= x 0.0)) information-gains)
      nil
      best-attribute)))

;; (def sub-data (filter #(= "povoljno" (get % :zaduzenje)) training-data))
;; (information-gain sub-data :stan :otplata) ;AKO JE INFORMACIONA DOBIT = 0, ONDA NE RADIMO DALJE GRANANJE
;; (information-gain sub-data :primanja :otplata) ;AKO JE INFORMACIONA DOBIT = 0, ONDA NE RADIMO DALJE GRANANJE
;; (every? (fn [x] (= x 0.0)) [0.0 0.0 0.0])
;; (require 'clojure.tools.trace)


(defn create-tree
  "Creates a decision tree according to training data using ID3 algorithm."
  [data out attributes]
  (if (= 1 (count (distinct (extract-column data out))))
    (first (distinct (extract-column data out)))
    (if (empty? attributes)
      (let [unique-target-values (map #(get % out) data)
            counts (frequencies unique-target-values)
            majority-class (->> counts
                                (apply max-key second)
                                first)]
        majority-class)
      (let [best-attribute (best-attribute data out attributes)]
        (if (= best-attribute nil)
           ;ako ne radimo dalje grananje, onda racunamo verovatnoce
          (let [unique-target-values (map #(get % out) data)
                counts (frequencies unique-target-values)
                majority-class (->> counts
                                    (apply max-key second)
                                    first)]
            majority-class)
          (let [attributes (remove #(= % best-attribute) attributes)]
            (->> (distinct (map #(get % best-attribute) data))
                 (reduce (fn [tree value]
                           (let [sub-data (filter #(= value (get % best-attribute)) data)
                                 subtree (create-tree sub-data out attributes)]
                             (assoc-in tree [best-attribute value] subtree)))
                         {best-attribute {}}))))))))


(defn tree-predict
  "Returns the prediction of an output variable for the entered entity, using created decision tree.
   If return value is nil, the tree cannot make prediction because the probabilies of all outcomes are equal."
  [tree entity]
  (if (map? tree)
    (let [attribute (first (keys tree))
          attribute-value (get entity attribute)]
      (tree-predict (get (get tree attribute) attribute-value) entity))
    tree))

(defn tree-predict-many
  "Returns the prediction of an output variable for all entities in entered array, using created decision tree.
   If value of output attribute is nil, the tree cannot make prediction because the probabilies of all outcomes are equal."
  [tree entities]
  (into [] (map (fn [row] (assoc row :job-fit (tree-predict tree row))) entities)))


(defn remove-column
  "Returns entered data withoud entered column"
  [data column-to-remove]
  (into [] (map #(dissoc % column-to-remove) data)))

(defn training-and-validation
  "Splits the dataset into two segments based on the provided proportion.
   If the proportion is 0.8, the training data will represent 80% of the data."
  [data proportion]
  (let [count (count data)
        index (Math/round (* proportion count))
        training-test-array (split-at index data)
        training-data (into [] (get training-test-array 0))
        validation-data (into [] (get training-test-array 1))]
    [training-data validation-data]))


(defn print-tree [tree depth]
  (doseq [[key value] tree]
    (if (= (type value) java.lang.String)
      (println (str (apply str (repeat depth "  ")) key ": " value))
      (println (str (apply str (repeat depth "  ")) key)))
    (when (map? value)
      (print-tree value (inc depth)))))

;***********************************************************
;              EVALUACIJA MODELA - ACCURACY
;***********************************************************

(defn calculate-accuracy
  "Evaluates the accuracy of predictions by comparing the predicted values with the actual values, 
   returning the percentage of correct predictions"
  [actuals predictions]
  (let [correct-predictions (into [] (filter (fn [prediction] 
                                               (let [actual (get actuals (.indexOf predictions prediction))] (= actual prediction))) predictions))
        total-predictions (count actuals)
        accuracy (double (/ (count correct-predictions) total-predictions))]
    accuracy))


;***********************************************************
;                     OSNOVNE FUNKCIJE 
;***********************************************************

(defn get-candidate
  "Returns the candidate's object that contains all relevant information, 
   based on the entered id."
  ([candidate-id]
   (into {} (filter (fn [candidate] (= (:id candidate) candidate-id)) (repository/get-candidates))))
  ([candidate-id candidates]
   (into {} (filter (fn [candidate] (= (:id candidate) candidate-id)) candidates))))

(defn get-ratings
  "Returns all of the candidate's ratings. 
   Candidates are rated based on selection criteria defined for the job."
  ([candidate-id]
   (into [] (filter (fn [rating] (= (:candidate-id rating) candidate-id)) (repository/get-ratings))))
  ([candidate-id ratings]
   (into [] (filter (fn [rating] (= (:candidate-id rating) candidate-id)) ratings))))

(defn get-candidates
  "Returns all candidates who applied for a specific job"
  ([job-id]
   (into [] (filter (fn [candidate] (= (:job-id candidate) job-id)) (repository/get-candidates))))
  ([job-id candidates]
   (into [] (filter (fn [candidate] (= (:job-id candidate) job-id)) candidates))))

(defn get-jobs-criteria
  "Returns all selection criteria for a specific job"
  ([job-id]
   (into [] (filter (fn [criteria] (= (:job-id criteria) job-id)) (repository/get-criteria))))
  ([job-id criteria]
   (into [] (filter (fn [criteria] (= (:job-id criteria) job-id)) criteria))))

(defn get-jobs-name
  "Returns the job name based on the entered id"
  ([job-id]
   (:name (into {} (filter (fn [job] (= (:id job) job-id)) (repository/get-jobs)))))
  ([job-id jobs]
   (:name (into {} (filter (fn [job] (= (:id job) job-id)) jobs)))))

(defn get-ponder
  "Returns a ponder value (criteria weight) for a specific criteria"
  ([job-id qualification-id]
   (if (or (<= job-id 0) (<= qualification-id 0))
     (str "error." " please insert valid id values")
     (double (:ponder (into {} (filter (fn [criteria] (and (= (:job-id criteria) job-id) (= (:qualification-id criteria) qualification-id))) (repository/get-criteria)))))))
  ([job-id qualification-id criteria]
   (if (or (<= job-id 0) (<= qualification-id 0))
     (str "error." " please insert valid id values")
     (double (:ponder (into {} (filter (fn [criteria] (and (= (:job-id criteria) job-id) (= (:qualification-id criteria) qualification-id))) criteria)))))))

;criteria-id = job-id + qualification-id
(defn get-ratings-of-criteria
  "Gets ratings of all candidates for a specific criteria"
  ([job-id qualification-id]
   (into [] (filter (fn [rating] (and (= (:job-id rating) job-id) (= (:qualification-id rating) qualification-id))) (repository/get-ratings))))
  ([job-id qualification-id ratings]
   (into [] (filter (fn [rating] (and (= (:job-id rating) job-id) (= (:qualification-id rating) qualification-id))) ratings))))


(defn get-ratings-of-job
  "Gets ratings of all candidates according to all criteria for a specific job"
  ([job-id]
   (into [] (filter (fn [rating] (= (:job-id rating) job-id)) (repository/get-ratings))))
  ([job-id ratings]
   (into [] (filter (fn [rating] (= (:job-id rating) job-id)) ratings))))

(defn get-qualification
  "Returns a specific qualification"
  ([qualification-id]
   (into {} (filter (fn [qual] (= (:id qual) qualification-id)) (repository/get-qualifications))))
  ([qualification-id qualifications]
   (into {} (filter (fn [qual] (= (:id qual) qualification-id)) qualifications))))


;***********************************************************
;                     NORMALIZACIJA 
;***********************************************************

(defn sum-of-all-ratings
  "Calculates sum of ratings of all candidates who have applied for a specific job according to a specific criteria"
  ([job-id qualification-id]
   (reduce + (map :value (get-ratings-of-criteria job-id qualification-id))))
  ([job-id qualification-id ratings]
   (reduce + (map :value (get-ratings-of-criteria job-id qualification-id ratings)))))

(defn get-normalized-ratings-of-criteria
  "Calculates normalized values of ratings according to a specific criteria"
  ([job-id qualification-id]
   (into [] (map (fn [row] (assoc row :normalized-value (/ (double (:value row)) (sum-of-all-ratings job-id qualification-id)))) (get-ratings-of-criteria job-id qualification-id))))
  ([job-id qualification-id ratings]
   (into [] (map (fn [row] (assoc row :normalized-value (/ (double (:value row)) (sum-of-all-ratings job-id qualification-id ratings)))) (get-ratings-of-criteria job-id qualification-id ratings)))))

(defn get-normalized-rating
  "Returns a rating with added field :normalized-value"
  ([rating]
   (into {} (assoc rating :normalized-value (/ (double (:value rating)) (sum-of-all-ratings (:job-id rating) (:qualification-id rating))))))
  ([rating ratings]
   (into {} (assoc rating :normalized-value (/ (double (:value rating)) (sum-of-all-ratings (:job-id rating) (:qualification-id rating) ratings))))))

(defn normalize-job-ratings
  "Normalizes all ratings of all candidates for a specific job"
  ([job-id]
   (let [ratings (get-ratings-of-job job-id)]
     (map (fn [row] (get-normalized-rating row)) ratings)))
  ([job-id ratings]
   (let [ratings-of-job (get-ratings-of-job job-id ratings)]
     (map (fn [row] (get-normalized-rating row ratings)) ratings-of-job))))


;***********************************************************
;                     AGREGACIJA OCENA 
;***********************************************************

(defn add-ponder-to-normalized-ratings
  "Adds a ponder to the normalized ratings of all candidates who applied for a specific job"
  ([job-id]
   (let [normalized-ratings (normalize-job-ratings job-id)]
     (into [] (map (fn [row] (assoc row :ponder (get-ponder job-id (:qualification-id row)))) normalized-ratings))))
  ([job-id ratings criteria]
   (let [normalized-ratings (normalize-job-ratings job-id ratings)]
     (into [] (map (fn [row] (assoc row :ponder (get-ponder job-id (:qualification-id row) criteria))) normalized-ratings)))))

(defn aggregate-candidate
  "Aggregates the overall rating of the candidate by using a weighted sum"
  ([candidate-id]
   (let [candidate (get-candidate candidate-id)
         ratings (add-ponder-to-normalized-ratings (:job-id candidate))]
     (assoc candidate :final-score (double (reduce + (map (fn [row] (* (:normalized-value row) (:ponder row))) (filter (fn [rating] (= (:candidate-id rating) candidate-id)) ratings)))))))
  ([candidate-id candidates ratings criteria]
   (let [candidate (get-candidate candidate-id candidates)
         normalized-ratings (add-ponder-to-normalized-ratings (:job-id candidate) ratings criteria)]
     (assoc candidate :final-score (double (reduce + (map (fn [row] (* (:normalized-value row) (:ponder row))) (filter (fn [rating] (= (:candidate-id rating) candidate-id)) normalized-ratings))))))))

(aggregate-candidate 1)

(aggregate-candidate 2)


;***********************************************************
;                   PODRSKA U ODLUCIVANJU 
;***********************************************************


(defn decision-support
  "Applies the method of multi-criteria decision-making and provides
   advices for the most suitable candidates for a specific job"
  ([job-id]
   (let [candidates (get-candidates job-id)]
     (into [] (sort-by :final-score (comparator >) (map (fn [row] (aggregate-candidate (:id row))) candidates)))))
  ([job-id candidates ratings criteria]
   (let [candidates-for-job (get-candidates job-id candidates)]
     (into [] (sort-by :final-score (comparator >) (map (fn [row] (aggregate-candidate (:id row) candidates ratings criteria)) candidates-for-job))))))

(defn round-to-decimal-places [num places]
  (Double/parseDouble (format (str "%." places "f") num)))

(defn selection-advice
  "Gives advice for the top two most suitable candidates for a specific job"
  ([job-id]
   (let [rated-candidates (decision-support job-id)]
     (let [first-candidate (get rated-candidates 0)
           second-candidate (get rated-candidates 1)]
       [(str "Top rated Candidate for " (get-jobs-name job-id) ": " (:firstname first-candidate) " " (:lastname first-candidate) " with final score of " (round-to-decimal-places (:final-score first-candidate) 4))
        (str "You should also consider " (:firstname second-candidate) " " (:lastname second-candidate) " with final score of " (round-to-decimal-places (:final-score second-candidate) 4) " for " (get-jobs-name job-id))])))
  ([job-id candidates ratings criteria jobs]
   (let [rated-candidates (decision-support job-id candidates ratings criteria)]
     (let [first-candidate (get rated-candidates 0)
           second-candidate (get rated-candidates 1)]
       [(str "Top rated Candidate for " (get-jobs-name job-id jobs) ": " (:firstname first-candidate) " " (:lastname first-candidate) " with final score of " (round-to-decimal-places (:final-score first-candidate) 4))
        (str "You should also consider " (:firstname second-candidate) " " (:lastname second-candidate) " with final score of " (round-to-decimal-places (:final-score second-candidate) 4) " for " (get-jobs-name job-id jobs))]))))


;***********************************************************
;        AHP - ODREDJIVANJE VAZNOSTI KRITERIJUMA
;***********************************************************
; u okviru ahp-ponders nalaze se preference donosilaca odluka 

(defn inverse-ponders
  "Inverts AHP ponders. If significance value on a specific position is x, then significance value on inverted position must be 1/x"
  [raw-ponders]
  (into [] (map (fn [obj] (let [position1 (get (:position obj) 0)
                                position2 (get (:position obj) 1)] (assoc obj :position [position2, position1] :significance (/ 1 (:significance obj))))) raw-ponders)))

(defn add-inverse-ponders
  "Adds inverted ponders to an array of initially created ponders"
  [raw-ponders]
  (into [] (concat raw-ponders (inverse-ponders raw-ponders))))

(defn get-ahp-ponders
  "Returns ahp ponders for a specific job"
  [job-id ahp-ponders]
  (into [] (filter (fn [row] (= (:job-id row) job-id)) ahp-ponders)))


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
  [job-id ahp-ponders]
  (let [raw-ponders (get-ahp-ponders job-id ahp-ponders)
        final-ponders (add-inverse-ponders raw-ponders)
        rows-cols-count (count ahp-ponders)
        init-matrix (create-matrix rows-cols-count)]
    (modify-ahp-matrix init-matrix final-ponders)))


(defn calculate-total-array
  "Calculates total array of weights for all criteria using AHP methodology"
  [job-id ahp-ponders]
  (let [ahp-matrix (create-ahp-matrix job-id ahp-ponders)
        array-sums (into [] (map (fn [arr] (reduce + arr)) ahp-matrix))
        total-sum (reduce + array-sums)]
    (into [] (map (fn [element] (/ element total-sum)) array-sums))))

(defn calculate-ahp
  "Calculates total ahp weights for criteria"
  [job-id ahp-ponders]
  (let [ahp-array (calculate-total-array job-id ahp-ponders)
        criteria (get-jobs-criteria job-id)]
    (into [] (map (fn [element] (assoc element :ahp-ponder (get ahp-array (.indexOf criteria element)))) criteria))))

