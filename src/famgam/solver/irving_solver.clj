(ns famgam.solver.irving-solver
  (:require [famgam.mastermind :as mm]
            [famgam.solver.common :as common]))

;;;; Irvings solution is based on minimizing the expected size of the
;;;; number of possible solutions remaining after the next guess. It
;;;; is notable that the `x-next-guess` algorithm is the same as in
;;;; the Knuth strategy. The only difference lies in the scoring of
;;;; the next guess.

(def irving-init-guess [0 0 1 2])

(defn weight [solutions guess]
  (let [scores (map (partial mm/score-guess guess) solutions)
        hit-count (map val (frequencies scores))
        total (count solutions)
        expected-values (map #(/ (* % %) total) hit-count)
        ev (apply + expected-values)]
    (- total ev)))

(defn irving-next-guess
  [solutions path]
  (if (>= 2 (count solutions))
    (first solutions)
    (let [guessed (set (map :guess path))
          unused (filter (complement guessed) common/all-numeric-combinations)
          weights (for [combo unused] [(weight solutions combo) combo])
          max-weight (apply max (map first weights))
          best (map second (filter #(= max-weight (first %)) weights))]
      (if-let [guess (some (partial (set solutions)) best)]
        guess
        (first best)))))
