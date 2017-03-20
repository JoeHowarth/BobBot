(defn index [coll]
  (cond
    (map? coll) (seq coll)
    (set? coll) (map vector coll coll)
    :else (map vector (iterate inc 0) coll)))
;; takes pred comparison
(defn pos
  [pred coll]
  (for [[i v] (index coll) :when (pred v)] i))

;; equality compaison
(defn pos
  [e coll
    (for [[i v] (index coll) :when (= e v)] i)])
