(ns famgam.solver.knuth-solver-test
  (:require [clojure.test :refer :all]
            [famgam.mastermind :as mm]
            [famgam.solver.knuth-solver :refer :all]))


(deftest weight-test
  (testing "the weight is all the guesses which do not score the
            given score when scored against 'guess'"
    (let [score {:black 2, :white 2}
          guess [1 2 3 4]
          solutions [[1 2 4 3] 
                     [4 2 3 1] 
                     [2 1 3 4] 
                     [1 4 3 2] 
                     [4 3 2 1] 
                     [2 1 4 3] 
                     [0 0 0 0] 
                    ]]
      (is (= 3 (weight solutions guess))))))

