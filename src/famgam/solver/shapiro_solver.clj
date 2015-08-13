(ns famgam.solver.shapiro-solver
  (:require [famgam.mastermind :as mm]
            [famgam.solver.common :as common]))

;;;; Shapiro's strategy simply guesess the first of the remaining
;;;; possible solutions. When the initial guess is [0 0 0 0] the
;;;; algorithm correctly discovers the solution in
;;;; a maximum of 9 guesses for a (4,6) game. The average number
;;;; of guesses is 5.765.
;;;; When the starting guess is [0 0 1 1] this is improved to a
;;;; maximum of 8 guesses and an average of 5.022 guesses per game.
;;;; An even better initial guess is [2 3 4 5] which guesses every
;;;; solution in a maximum of 7 guesses and an average of 4.664
;;;; guesses.

(def shapiro-init-guess [0 0 1 1])

(defn shapiro-next-guess [solutions _]
  (first solutions))


