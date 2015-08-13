# mastermind

A Clojure library to use for Mastermind strategy simulations.

## Usage

Example usage:

```clojure
(require '[famgam.solver.common :as common]
         '[famgam.solver.knuth-solver :as ks])

; famgam.solver.common is setup to solve numeric secret codes
(def secret-code [0 5 2 5]) 
                            

; Find a solution using Knuth's Mastermind solving algorithm
; and Knuth's optimized initial guess.
(def solution
  (common/find-solution
     ks/knuth-next-guess ks/knuth-init-guess secret-code))

; Print a human-readable representation of the solution
(println (common/pprint-game solution))

;=>
; Guess  |(B,W)|Remaining
; -------|-----|---------
; 0,0,1,1|(1,0)|1296     
; 0,2,3,3|(1,1)|256      
; 3,4,1,3|(0,0)|42       
; 0,5,2,5|(4,0)|3
```

