(ns clj-xlsxio.low-level-write
  (:refer-clojure :exlude [name])
  (:require [clojure.java.io :as io])
  (:import [com.sun.jna NativeLibrary Pointer]
           [java.util Date]
           [org.joda.time DateTime]))

(try
  (do
    (def z (NativeLibrary/getInstance "z"))
    (def minizip (NativeLibrary/getInstance "minizip"))
    (def libxlsxio-write (NativeLibrary/getInstance "xlsxio_write")))
  (catch Exception e 
    (do
      (println "============================================================================
               We've had a problem loading the native libraries.
               This library has two dependencies:
               minizip and libz (which is used by minizip)
               All of them are bundled in this jar library,
               however if you are having issues loading the shared objects consider
               installing them on your system.

               It is important to notice that all of these bundled native libraries were
               compiled with GNU libc standard library. If you are on a system based on musl
               (like Alpine Linux ) or another standard library you WILL
               need to install those 3 dependencies on your system.
               ============================================================================")
      (pr e))))

(defn open
  ^Pointer
  ([^String filename ^String sheetname]
   (let [res
         (.invoke (.getFunction libxlsxio-write "xlsxiowrite_open") Pointer (to-array [filename sheetname]))]
     (if (= res Pointer/NULL)
       (throw (RuntimeException. "Error on xlsxiowrite_open, returned NULL."))
       res)))
  ([^String filename]
   (open filename nil)))

(defn close
  ^Long
  [^Pointer handle]
  (let [res
        (.invoke (.getFunction libxlsxio-write "xlsxiowrite_close") Long (to-array [handle]))]
    (if (= res 0)
      (throw (RuntimeException. (str "Error on xlsxiowrite_close, returned " res)))
      res)))

(defn add-column
  ^Void
  ([^Pointer handle ^String name ^Long width]
   (.invoke (.getFunction libxlsxio-write "xlsxiowrite_add_column") Void (to-array [handle name width])))
  ([^Pointer handle ^String name]
   (.invoke (.getFunction libxlsxio-write "xlsxiowrite_add_column") Void (to-array [handle name]))))

(defn next-row
  ^Void
  [^Pointer handle]
  (.invoke (.getFunction libxlsxio-write "xlsxiowrite_next_row") Void (to-array [handle])))

(defn add-cell-string
  ^Void
  [^Pointer handle ^String value]
  (.invoke (.getFunction libxlsxio-write "xlsxiowrite_add_cell_string") Void (to-array [handle value])))

(defn add-cell-int
  ^Void
  [^Pointer handle ^Long value]
  (.invoke (.getFunction libxlsxio-write "xlsxiowrite_add_cell_int") Void (to-array [handle value])))

(defn add-cell-float
  ^Void
  [^Pointer handle ^double value]
  (.invoke (.getFunction libxlsxio-write "xlsxiowrite_add_cell_float") Void (to-array [handle value])))

(defn add-cell-double
  ^Void
  [^Pointer handle ^double value]
  (add-cell-float handle value))

(defn add-cell-datetime
  ^Void
  [^Pointer handle ^long value]
  (.invoke (.getFunction libxlsxio-write "xlsxiowrite_add_cell_datetime") Void (to-array [handle value])))

(defprotocol AddCell
  (add-cell-generic [generic handle]))

(extend-protocol AddCell
  String   (add-cell-generic [value handle]
             (add-cell-string handle value))
  Double   (add-cell-generic [value handle]
             (add-cell-float handle value))
  Long     (add-cell-generic [value handle]
             (add-cell-int handle value))
  Date     (add-cell-generic [value handle]
             (add-cell-datetime handle (/ (.getTime value) 1000)))
  DateTime (add-cell-generic [value handle]
             (add-cell-datetime handle (/ (.getMillis value) 1000))))

(defn add-cell
  ^Void
  [^Pointer handle value]
  (add-cell-generic value handle))
