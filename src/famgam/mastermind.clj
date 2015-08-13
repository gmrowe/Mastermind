(ns famgam.mastermind
  (:require [clojure.set :as set]))

;;;; famgam.mastermind contains a minimal set of fuctions for scoring
;;;; a Mastermind game.

(defn rand-seq [coll n]
  (repeatedly n #(rand-nth coll)))

(defn counting [p coll]
  (count (filter p coll)))

(defn score-guess [target guess]
  (let [black (counting identity (map = target guess))
        all-matches (apply + (map #(min (counting #{%} target) (counting #{%} guess))
                                  (set/intersection (set target) (set guess))))
        white (- all-matches black)]
    {:black black, :white white}))

(def colors [:red :orange :yellow :green :blue :purple])

