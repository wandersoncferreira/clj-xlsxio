(ns basic-read
  (:require [clj-xlsxio.core :as read]))

(defn basic-read
  []
  (println "Basic reading:")
  (pr (read/read-xlsx "examples/test.xlsx"))
  (println)
  (println "Reading skipping empty rows:")
  (pr (read/read-xlsx "examples/test.xlsx" :skip read/skip-empty-rows))
  (println)
  (println "Reading skipping empty cells:")
  (pr (read/read-xlsx "examples/test.xlsx" :skip read/skip-empty-cells))
  (println)
  (println "Reading skipping all empty:")
  (pr (read/read-xlsx "examples/test.xlsx" :skip read/skip-all-empty))
  (println)
  (println "Reading skipping all extra")
  (pr (read/read-xlsx "examples/test.xlsx" :skip read/skip-extra-cells))
  (println)
  )
