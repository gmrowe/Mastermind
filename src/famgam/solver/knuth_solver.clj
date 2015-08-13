(ns famgam.solver.knuth-solver
  (:require [famgam.mastermind :as mm]
            [famgam.solver.common :as common]))

;;;; The Knuth guess strategy is designed to maximize the minimum
;;;; solutions that are eliminated with every guess. This strategy
;;;; will always guess the correct result in a standard
;;;; (4,6) Mastermind game in 5 guesses or fewer.
;;;;
;;;; The algorithm is described in the Mastermind Wikipedia
;;;; [entry](https://en.wikipedia.org/wiki/Mastermind_%28board_game%29)
;;;; Note: The algorithm as described uses 1 based indexing, the
;;;; The implementation below uses 0 based indexing.
;;;; 
;;;; "In 1977, Donald Knuth demonstrated that the codebreaker can
;;;; solve the pattern in five moves or fewer, using an algorithm that
;;;; progressively reduced the number of possible patterns.[9] The
;;;; algorithm works as follows:
;;;;
;;;;   1. Create the set S of 1296 possible codes, 1111,1112,.., 6666.
;;;;   2. Start with initial guess 1122 (Knuth gives examples showing
;;;;      that some other first guesses such as 1123, 1234 do not win
;;;;      in five tries on every code).
;;;;   3. Play the guess to get a response of colored and white pegs.
;;;;   4. If the response is four colored pegs, the game is won, the
;;;;      algorithm terminates.
;;;;   5. Otherwise, remove from S any code that would not give the
;;;;      same response if it (the guess) were the code.
;;;;   6. Apply minimax technique to find a next guess as follows:
;;;;      For each possible guess, that is, any unused code of the
;;;;      1296 not just those in S, calculate how many possibilities
;;;;      in S would be eliminated for each possible colored/white
;;;;      peg score. The score of a guess is the minimum number of
;;;;      possibilities it might eliminate from S. A single pass
;;;;      through S for each unused code of the 1296 will provide a
;;;;      hit count for each colored/white peg score found ; the
;;;;      colored/white peg score with the highest hit count will
;;;;      eliminate the fewest possibilities ; calculate the score
;;;;      of a guess by using "minimum eliminated" = "count of elements
;;;;      in S" - (minus) "highest hit count". From the set of guesses
;;;;      with the maximum score, select one as the next guess, choosing
;;;;      a member of S whenever possible. (Knuth follows the convention
;;;;      of choosing the guess with the least numeric value e.g. 2345
;;;;      is lower than 3456. Knuth also gives an example showing that
;;;;      in some cases no member of S will be among the highest scoring
;;;;      guesses and thus the guess cannot win on the next turn, yet
;;;;      will be necessary to assure a win in five.)
;;;;   7. Repeat from step 3.

(def knuth-init-guess
  "This is the initial guess proposed by Knuth. It provides optimal
  performance in minimizing the number of guesses required to find a
  solution using this strategy."
  [0 0 1 1])

(defn weight
  "Weight assigns a weight to a solution based on the minimum number
  of remaining potential solutions it would eliminate if it were
  guessed."
  [solutions guess]
  (let [scores (map (partial mm/score-guess guess) solutions)
        hit-count (frequencies scores)
        max-hit-count (apply max (map val hit-count))]
    (- (count solutions) max-hit-count)))

(defn knuth-next-guess
  "Scores all unguessed alternatives by weight and returns the highest
  scoring. In the event of a tie, the algorithm first chooses the
  first (alphabetical) candidate that is a possible solution. If none
  of the highest scoring candidates are possible soltions, it will
  chose the first (alphabetical) best scoring alternative. The
  `solutions` parameter should be a seq of all remaining viable solutions.
  The `path` parameter is a vector of maps. See famgam.solver.common for
  a descrition of the map."
  [solutions path]
  (let [guessed (set (map :guess path))
        unused (filter (complement guessed) common/all-numeric-combinations)
        weights (for [combo unused] [(weight solutions combo) combo])
        max-weight (apply max (map first weights))
        best (map second (filter #(= max-weight (first %)) weights))]
    (if-let [guess (some (partial (set solutions)) best)]
      guess
      (first best))))

(defn knuth-exhaustive-simulation
  "Runs famgam.solver.comon/exhaustive-simulation using the
  knuth-next-guess strategy."
  []
  (common/exhaustive-simulation knuth-next-guess knuth-init-guess))
