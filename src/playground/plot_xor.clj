;pull in doc stuff
(apply require clojure.main/repl-requires)

(defn f-values [f xs ys]
  (for [x (range xs) y (range ys)]
    [x y (rem (f x y) 256)]))

; GUI shit
; init frame
(def frame (java.awt.Frame.))
(.setVisible frame true)
(.setSize frame (java.awt.Dimension. 200 200))
(def gfx (.getGraphics frame))

; funcs
(defn clear [g] (.clearRect g 0 0 256 256))

;; need to redefine gfx for some reason...
(defn draw-values [f xs ys]
          (clear gfx)
          (.setSize frame (java.awt.Dimension. (* 2 xs) (* 2 ys)))
          (def gfx (.getGraphics frame))
          (doseq [[x y v] (f-values f xs ys)]
            (.setColor gfx (java.awt.Color. v v v))
            (.fillRect gfx (* 2 x) (* 2 y) 2 2)))




; SILLY STUFF
(.fillRect gfx 100 100 50 75)
(.setColor gfx (java.awt.Color. 255 128 0))
(.fillRect gfx 100 100 75 50)
;find frame method to make visible
(for
  [meth (.getMethods java.awt.Frame)
   :let [name (.getName meth)]
   :when (re-find #"Vis" name)]
 name)

(defn xors [max-x max-y]
  (for [x (range max-x) y (range max-y)]
       [x y (bit-xor x y)]))
