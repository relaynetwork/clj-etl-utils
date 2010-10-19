(ns clj-etl-utils.lang-test
  (:require [clj-etl-utils.lang :as lang])
  (:use [clojure.test]))

(deftest test-make-periodic-invoker
  (let [stat    (atom [])
        trigger (lang/make-periodic-invoker 10
                                            (fn [count val]
                                              (swap! stat conj val)))]
    (dotimes [ii 100]
      (trigger :hit ii))
    (is (= 10 (count @stat)))))

;; (test-make-periodic-invoker)

(comment
  (let [stat    (atom [])
        trigger (lang/make-periodic-invoker 10
                                            (fn [count val]
                                              (swap! stat conj [count val])))]
    (dotimes [ii 100]
      (trigger :hit ii))
    @stat)


  (def *timer* (lang/make-periodic-invoker
                10
                (fn [val & args]
                  (printf "triggered: val=%s args=%s\n" val args))))

  (dotimes [ii 10] (*timer*))
  (*timer* :final)
  (*timer* :invoke 1 2 3)
  (*timer* :state)
  (*timer* :set 19)
  (*timer* :reset)

  (let [total    1000
        period     100
        progress (lang/make-periodic-invoker
                  period
                  (fn [val & [is-done]]
                    (if (= is-done :done)
                      (printf "All Done! %d\n" val)
                      (printf "So far we did %d, we are  %3.2f%% complete.\n" val (* 100.0 (/ val 1.0 total))))))]
    (dotimes [ii total]
      (progress))
    (progress :final :done))


  )