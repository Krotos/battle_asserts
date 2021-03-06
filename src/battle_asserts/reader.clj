(ns battle-asserts.reader
  (:require [multicode.core :as mc]))

(defn- get-clojure-test
  [test-code]
  (letfn [(find-test [code] (first (filter #(= 'test-asserts (nth % 1)) code)))
          (extract-body [func] (drop 2 func))]
    (-> test-code find-test extract-body)))

(defn- generate-asserts
  [langs test-code]
  (let [clojure-test (get-clojure-test test-code)]
    (reduce #(assoc %1 %2 (mc/prettify-code %2 clojure-test))
            {}
            langs)))

(defn transform
  [data test-code]
  (let [langs (map keyword (get-in data [:multicode_checks :langs]))
        issue (dissoc data :multicode_checks)]
    (if (not (empty? langs))
      (merge issue {:checks (generate-asserts langs test-code)})
      issue)))
