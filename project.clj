(defproject playground "0.1.0-SNAPSHOT"
  :description "Little bot thingy"
  :url "https://github.com/JoeHowarth/clojure_playground"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [proto-repl "0.3.1"]
                 [twitter-api "1.8.0"]
                 [environ "1.1.0"]
                 [overtone/at-at "1.2.0"]]

                ;  [org.clojure/tools.logging "0.3.1"]
                ;  [org.slf4j/slf4j-log4j12 "1.7.12"]
                ;  [log4j/log4j "1.2.17"]]
  :min-lein-version "2.0.0()"
  :target-path "target/%s"
  :main playground.markov
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[com.jakemccrary/lein-test-refresh "0.19.0"]]}}
  :plugins [[lein-environ "1.1.0"]])
