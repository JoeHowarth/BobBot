(ns playground.markov)


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




;; rolled into text->word-chain
(def ex-prefix ["the" "Pobble"])
(def example "And the Golden Grouse And the Pobble who")
(def words (clojure.string/split example #" "))
(def word-transitions (partition-all 3 1 words))
; (chain->text (walk-chain ex-prefix (text->word-chain example) ex-prefix))
