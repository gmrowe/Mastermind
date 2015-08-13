(ns famgam.mastermind-test
  (:require [clojure.test :refer :all]
            [famgam.mastermind :refer :all]))

(deftest rand-seq-test
  (let [coll [:a :b :c :d]
        n 6
        s (rand-seq coll n)]
    (testing "seq returned should always have n elements"
      (is (= n (count s))))
    (testing "all elements in returned seq are in coll"
      (is (every? #(some #{%} coll) s)))))

(deftest score-guess-test
  (testing "score-guess should score hits and misses"
    (let [score (score-guess [:a :b :c :d] [:d :b :c :a])]
      (is (= 2 (get score :black)))
      (is (= 2 (get score :white))))
    (let [score (score-guess [:a :b :c :d] [:w :x :y :z])]
      (is (zero? (get score :black)))
      (is (zero? (get score :white))))
    (let [score (score-guess [:a :b :c :d] [:a :b :x :y])]
      (is (= 2 (get score :black)))
      (is (zero? (get score :white))))
    (let [score (score-guess [:a :b :c :d] [:b :a :x :y])]
      (is (zero? (get score :black)))
      (is (= 2 (get score :white))))))
