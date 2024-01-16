# clojureselect

Clojure Select stands as a powerful ally for HR professionals, integrating sophisticated algorithms to streamline decision-making. By offering insightful predictions and multi-criteria analysis, the system optimizes the candidate selection process. It serves as a valuable support tool, combining mathematical precision with human expertise to elevate decision-making in the dynamic landscape of HR.

![logo](images/logo.png?raw=true "Clojure Select")

# Components

The Clojure Select system has three core components that collectively support the HR sector in decision-making. Those components are: 
1. Decision Tree Algorithm 
2. Multi-Criteria Decision Analysis Algorithms 
3. AHP (Analytic Hierarchy Process) Algorithm


![components](images/components.png?raw=true "Clojure Select Components")


# Examples

## Decision Tree Example

Let’s assume we have a certain training dataset. In the image below, a portion of this data is shown. Complete training dataset contains information about 1018 former candidates. Note that all the data is of qualitative ordinal type. 

![candidates](images/candidates.png?raw=true "Candidates Training Data")

Each candidate is described by nine attributes: Education, Work Experience, Technical Skills, Soft Skills, References, Communication Skills, Problem Solving Ability, Cultural Fit and Learning Ability. After a certain period of work, HR managers evaluated each candidate based on the output attribute Job Fit. Based on this data, the decision tree algorithm should infer certain patterns in the data, determining which set of input data leads to which value of the output attribute. This is intended to help HR managers decide about future candidates for whom the output attribute's value is unknown. In the following code the training data has been read from a CSV file. Based on loaded training data, a decision tree has been created.

```
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
```

We can use the print-tree function for a visual representation of the decision tree. Given that this tree is complex, only a portion of the tree is shown below. 

```
:work-experience
  Medium
    :learning-ability
      Limited
        :cultural-fit
          Moderate Fit
            :technical-skills
              Beginner
                :soft-skills
                  Low: Low Fit
                  Medium: Low Fit
                  High
                    :education
                      Bachelor's Degree: Good Fit
                      High School: Low Fit
                      Postgraduate Education: Low Fit
              Advanced
                :soft-skills
                  Low: Low Fit
                  Medium: Good Fit
                  High
                    :problem-solving-ability
                      High: Good Fit
                      Medium: Good Fit
                      Low: Low Fit
              Intermediate
                :references
                  Yes
                    :soft-skills
                      High: Good Fit
                      Low: Low Fit
                  No: Low Fit
          Low Fit
            :education
              High School: Low Fit
              Bachelor's Degree: Low Fit
              Postgraduate Education
                :technical-skills
                  Advanced
                    :communication-skills
                      Excellent: Good Fit
                      Good
                        :soft-skills
                          Medium: Low Fit
                          High: Good Fit
                      Needs Improvement: Low Fit
                  Beginner: Low Fit
                  Intermediate: Low Fit
          High Fit
            :problem-solving-ability
              Low
                :technical-skills
                  Advanced
                    :communication-skills
                      Good: Good Fit
                      Needs Improvement: Low Fit
                      Excellent: Good Fit
                  Intermediate: Low Fit
                  Beginner: Low Fit
                  ...
```

Based on the generated decision tree, we can predict the output attribute value for candidates whose output attribute value is unknown. Note that the predicted value of Job Fit attribute for first candidate is "High Fit", while for second candidate it is "Good Fit".

```
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
```

```
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
```

By using presented algorithms, it is possible to make predictions about the success of candidates in future job performance, significantly facilitating the decision-making process in the HR sector.

## Simple Decision Tree Example

Let’s take a look at a simpler example. Let's say we want to predict whether a bank client will be able to repay a loan. Based on historical data shown in the image below, we will create a decision tree.

![loan-repayment-training](images/loan-repayment-training.png?raw=true "Loan-Repayment Training Data")

Each observation is described by 3 ordinal attributes: Debts, Income and Apartment. In Clojure Select system, we can generate a decision tree using this data. By calling the print-tree function, we will obtain the display of a tree shown below. 

```
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
```

```
:debts
  critical: no
  acceptable
    :apartment
      yes: yes
      no: no
  good: no
```
We can represent the created tree graphically, as shown in the image below.

![simple-tree](images/simple-tree.png?raw=true "Loan-Repayment Decision Tree")

We can now make simple predictions for new entities, for which the value of the output attribute is unknown. Return value of the given code will be "no".

```
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
  (tree-predict tree test-entity)) => "no"
```


## Multi-Criteria Decision Analysis

### Description 

Multi-criteria decision analysis (MCDA) is a decision-making approach that involves evaluating and choosing among multiple alternatives based on a set of criteria or attributes. The decision-making process becomes significantly more complex when there are multiple criteria based on which a decision needs to be made. In multi-criteria decision analysis, each alternative is described by a set of criteria that are considered when making decisions. These criteria differ in their importance for the final decision. We refer 
to these importance as criteria weights or ponders. Each criterion has its ponder, describing the relative significance of that criterion compared to others. This means that the total sum of all ponders assigned to defined criteria must be equal to one. This way, each criterion will participate in the decision-making process proportionally to its actual importance for the final decision.

In the context of the selection process, the decision about the most suitable candidate will be made based on various criteria, each used to evaluate every candidate. In the image below, we can see the model we will use for further analysis of multi-criteria decision-making theory.

![model](images/model.png?raw=true "Model")

### Decision Support Example

Let’s assume that in the relational database, we have the following values stored in the Ratings table: 

![ratings](images/ratings.png?raw=true "Ratings")

Based on these values, it is necessary to **first perform the normalization of criteria, so that all criteria take values from the same range**. After that, it is required to **aggregate all ratings into a composite indicator for each candidate**. Note that the ponders of criteria are also stored in the database in the Criteria table.

The decision-support function will return a sorted list of candidates with final ratings, which we have called the composite indicators. The candidates in the returned list are sorted from the most suitable to the least suitable for the specific job.

```
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
```
Let's look at the result of the above-mentioned code: 

```
[{:id 2,
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
  :final-score 0.30416112693890474}]
```

Note that the decision-support function will return a sorted list of candidates with final ratings, which we have called the composite indicators. The candidates in the returned list are sorted from the most suitable to the least suitable for a specific job.

## AHP Algorithm 

### Description 

In decision-making theory AHP stans for Analytic Hierarchy Process. It is a decision-making technique that helps individuals and groups make complex decisions by structuring them into a hierarchical model. AHP provides a structured and systematic approach to decision-making, particularly in situations where multiple criteria and stakeholders are involved. In multi-criteria decision analysis, the AHP algorithm can be used to determine the significance of criteria. The decision-makers uses Saaty’s 9-point scale to indicate how important one criterion is relative to another, employing pairwise comparisons.

### Example

Let’s assume we have three criteria for a specific job: Education, Work Experience and Cultural Fit. Decision-makers assess the importance of each criterion relative to another, and these values are entered into AHP matrix, as shown in the image below.

![ahp-init](images/ahp-init.png?raw=true "Initial AHP Matrix")

A characteristic of the AHP matrix is that inverse values are symmetrically located across the diagonal. **If criterion A is 3 times more significant than criterion B, then criterion B is 1/3 as significant as criterion A**. Additionally, the values on the main diagonal are always equal to 1 because they represent the comparison of a criterion with itself.

The obtained matrix will be used to calculate the importance of each criterion. The AHP method is shown in the image below. In the image, we see that the AHP method involves summing the values in each row of the matrix. These values are called initial sums. After that, each initial sum is divided by the total value of all initial sums to calculate their relative values, which will sum up to one. These values obtained in this way represent the final ponders for each criterion.

![ahp-method](images/ahp-method.png?raw=true "AHP Method")

Let's call calculate-ahp function:

```
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
```

After calling calculate-ahp function, the Clojure Select system will return the criteria with new ponders obtained through the mentioned AHP method, as shown below:

```
[{:job-id 1, :qualification-id 1, :ponder 0.5, :ahp-ponder 0.5294117647058824}
 {:job-id 1, :qualification-id 2, :ponder 0.3, :ahp-ponder 0.16176470588235292}
 {:job-id 1, :qualification-id 3, :ponder 0.2, :ahp-ponder 0.3088235294117647}]
```

## License

Copyright © 2024 dusantavic
