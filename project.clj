(defproject clojureselect "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [com.github.seancorfield/next.jdbc "1.3.894"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [mysql/mysql-connector-java "8.0.33"]
                 [midje "1.10.9" :exclusions [org.clojure/tools.logging]]
                 [org.clojure/tools.trace "0.7.9"]
                 [dk.ative/docjure "1.14.0"]
  ]
  :main ^:skip-aot clojureselect.business-logic
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
