(ns playground.markov
  (:require [twitter.api.restful :as twitter]
            [twitter.oauth :as twitter-oauth]
            [overtone.at-at :as overtone]
            [environ.core :refer [env]]))

(def prefix-list ["On the" "They went" "And all" "We think"
                  "For every" "No other" "To a" "And every"
                  "We, too," "For his" "And the" "But the"
                  "Are the" "The Pobble" "For the" "When we"
                  "In the" "Yet we" "With only" "Are the"
                  "Though the"  "And when"
                  "We sit" "And this" "No other" "With a"
                  "And at" "What a" "Of the"
                  "O please" "So that" "And all" "When they"
                  "But before" "Whoso had" "And nobody" "And it's"
                  "For any" "For example," "Also in" "In contrast"])

(def files ["quangle-wangle.txt"
            "jumblies.txt" "pelican.txt"
            "pobble.txt"])

;; from profiles.clj
(def my-creds (twitter-oauth/make-oauth-creds (env :app-consumer-key)
                                              (env :app-consumer-secret)
                                              (env :user-access-token)
                                              (env :user-access-secret)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;; functions ;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; make markov pairs from word-transitions
(defn word-chain [word-transitions]
  (reduce (fn [r t] (merge-with clojure.set/union r
                        (let [[a b c] t]
                          {[a b] (if c #{c} #{})})))
          {}
          word-transitions))

;; turns input string into word-chain
(defn text->word-chain [s]
  (let [words (clojure.string/split s #"[\s|\n]")
        word-transitions (partition-all 3 1 words)]
    (word-chain word-transitions)))

;; results chain to text, converts a results chain to string
;; ex: "hi" "bob" -> "hi bob"
(defn chain->text [chain]
  (apply str (interpose " " chain)))

;; walk word chain
;;    ensure results <= 140 chars long (tweet length)
(defn walk-chain
  [prefix chain result]
  (let [suffixes (get chain prefix)]
    (if (empty? suffixes)
      result
      (let [suffix (first (shuffle suffixes))
            new-prefix [(last prefix) suffix]
            result-w-spaces (chain->text result)
            len-result (count result-w-spaces)
            len-suffix (count suffix)
            new-len (+ len-result len-suffix)]
        (if (>= new-len 140)
          result
          (recur new-prefix chain (conj result suffix)))))))

;; make full text from given prefix string
(defn generate-text
  [inp-str chain]
  (let [prefix (clojure.string/split inp-str #" ")]
    (chain->text (walk-chain prefix chain prefix))))

(defn process-file [fname]
  (text->word-chain
    (slurp (clojure.java.io/resource fname))))

;; creates chain based on leary texts
(def functional-leary
  (apply merge-with clojure.set/union
    (map process-file files)))

(defn end-at-last-punctuation [text]
  (let [trimmed-to-last-punct (apply str (re-seq #"[\s\w]+[^.!?,]*[.!?,]" text))
        trimmed-to-last-word (apply str (re-seq #".*[^a-zA-Z]+" text))
        result-text (if (empty? trimmed-to-last-punct)
                      trimmed-to-last-word
                      trimmed-to-last-punct)
        cleaned-text (clojure.string/replace result-text #"[,| ]$" ".")]
    (clojure.string/replace cleaned-text #"\"" "'")))

(defn tweet-text []
  (let [text (generate-text (-> prefix-list shuffle first) functional-leary)]
    (end-at-last-punctuation text)))

(defn status-update []
  (let [tweet (tweet-text)]
    (println "generated tweet is :" tweet)
    (println "char count is:" (count tweet))
    (when (not-empty tweet)
      (try (twitter/statuses-update :oauth-creds my-creds
                                    :params {:status tweet})
           (catch Exception e (println "Oh no! " (.getMessage e)))))))

(def my-pool (overtone/mk-pool))

(defn -main [& args]
  ;; every 8 hours
  (println "Started up")
  (println (tweet-text))
  (overtone/every (* 1000 60 60 8) #(println (status-update)) my-pool))

;; rolled into text->word-chain
(def ex-prefix ["the" "Pobble"])
(def example "And the Golden Grouse And the Pobble who")
(def words (clojure.string/split example #" "))
(def word-transitions (partition-all 3 1 words))
; (chain->text (walk-chain ex-prefix (text->word-chain example) ex-prefix))
