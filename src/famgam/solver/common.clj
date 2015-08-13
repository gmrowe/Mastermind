(ns famgam.solver.common
  (:require [famgam.mastermind :as mm]
            [clojure.math.combinatorics :as combo]
            [clojure.set :as set]
            [clojure.string :as str]))

;;;; This ns offers an interface into a common api to use
;;;; to test different Mastermind guessing strategies. 
;;;; The heart of this interface is the find-solution
;;;; function which takes a `next-guess-strategy` function
;;;; argument. The `next-guess-strategy` is passed a list of current
;;;; valid solutions and a vector of maps, where each map
;;;; contains a guess, the score returned from that guess,
;;;; and the number of valid solutions which remain. Using
;;;; this data, the next-guess-functions should use some
;;;; logic to formulate a guess.

;; As a default for these experiments we assume
;; a standard Mastermind game with 6 colors and
;; a code length of 4 (4, 6).
;;
;; TODO: create alternate aritys where the code
;;       length can be passed in for more difficult
;;       strategies
(def code-length
  "The length of the secret code"
  4)

(def all-numeric-combinations
    "Every (4,6) code in ascending order:
     ((0 0 0 0) (0 0 0 1) (0 0 0 2) ... (5 5 5 5)"
   (combo/selections (range (count mm/colors)) code-length))

(defn winner?
   "Returns true if the score represents a winning game.
   A winning game is represented by a maximal number of 'hits'"
   [score]
  (= code-length (:hits score)))


(defn find-solution
  "Takes a target and next-guess-strategy and repeatedly calls
  next-guess-strategy with the remaining possible solutions and set of
  previous guesses until next-guess-strategy guesses the target
  sequence. `find-solution` returns a vector of maps representing each
  guess that was made before the game was won. The map defines the
  following keys:
    :guess - a list representing the guess that was made
    :score - a map with mappings for :hits and :misses for each guess
    :solution-count - the number of valid solutions remaining before this
                      guess."
  [next-guess-strategy init-guess target]
  (loop [solutions all-numeric-combinations
         guess init-guess
         path []]
    (let [score (mm/score-guess target guess)
          path' (conj path
                      {:guess guess
                       :score score
                       :solution-count (count solutions)})]
      (if (winner? score)
        path'
        (let [solutions' (filter #(= score (mm/score-guess guess %)) solutions)
              guess' (next-guess-strategy solutions' path')]
          (recur solutions'  guess' path'))))))

(defn rand-code
  "Returns a random code-length code that can be used as a random
  target code or a random guess."
  []
  (rand-nth all-numeric-combinations))

(defn rand-game-simulation
  "Calls `find-solution` using a random target code and the
  next-guess-strategy."
  [next-guess-strategy init-guess]
  (find-solution next-guess-strategy init-guess (rand-code)))

(defn monte-carlo-simulations
  "Runs n rand-game-simulations and returns the results as a list."
  [n next-guess-strategy init-guess]
  (repeatedly n #(rand-game-simulation next-guess-strategy init-guess)))

(defn exhaustive-simulation
  "Calls find-solution with every possible starting target sequence
  using the next-guess-strategy. Returns the result as a list."
  [next-guess-strategy init-guess]
  (map (partial find-solution next-guess-strategy init-guess)
       all-numeric-combinations))

(defn pprint-game
  "Takes a game (the result of a call to `find-solution`) and returns a
  well formatted, human-readable String."
  [game]
  (letfn [(format-row [row]
            (let [{:keys [guess score solution-count]} row]
              [(str/join "," guess)
               (str "("(:black score) "," (:white score) ")")
               (str solution-count)]))
          (tableify [row] (apply format "%-7s|%-5s|%-9s" row))]
    (let [header ["Guess" "(B,W)" "Remaining"]
          separator ["-------" "-----" "---------"]]
      (->> (map format-row game)
           (concat [header separator])
           (map tableify)
           (str/join \newline)))))
