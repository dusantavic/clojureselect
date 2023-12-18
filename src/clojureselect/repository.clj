(ns clojureselect.repository 
  (:require [next.jdbc :as jdbc-next]
            [next.jdbc.result-set :as rs]))

(def db-spec
  {:dbtype "mysql"
   :dbname "clojureselect"
   :user "root"
   :password "dusan"
   :host "localhost"
   :port 3306})

(def db (jdbc-next/get-datasource db-spec))

(defn get-candidate
  [candidate-id]
  (jdbc-next/execute-one! db
                          ["SELECT * FROM candidates WHERE id = ?" candidate-id] {:builder-fn rs/as-unqualified-maps}))

(defn get-candidates []
  (jdbc-next/execute! db
                                ["SELECT * FROM candidates"] {:builder-fn rs/as-unqualified-maps}))

(defn get-jobs []
  (jdbc-next/execute! db
                          ["SELECT * FROM jobs"] {:builder-fn rs/as-unqualified-maps}))

(defn get-ratings []
  (jdbc-next/execute! db
                          ["SELECT * FROM ratings"] {:builder-fn rs/as-unqualified-maps}))

(defn get-criteria []
  (jdbc-next/execute! db
                          ["SELECT * FROM criteria"] {:builder-fn rs/as-unqualified-maps}))

(defn get-qualifications []
  (jdbc-next/execute! db
                          ["SELECT * FROM qualifications"] {:builder-fn rs/as-unqualified-maps}))

(defn get-employments []
  (jdbc-next/execute! db
                      ["SELECT * FROM employments"] {:builder-fn rs/as-unqualified-maps}))
