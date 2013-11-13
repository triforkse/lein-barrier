(ns leiningen.barrier
  (:require
   [leiningen.core.main :as main]
   [clojure.tools.namespace.find :as ns-find]
   [clojure.tools.namespace.parse :as parse]
   [clojure.tools.namespace.dependency :as dep]
   [robert.hooke]
   [leiningen.test])
  (:import
   [java.io File]))


(def ^:dynamic *exit-after-barrier* true)
(def ^:dynamic *ansi-colors-enabled* true)
(def colors {:green "\033[32m" :red "\033[31m" :reset "\033[0m"})

(defn get-color [color]
  (when *ansi-colors-enabled*
    (get colors color)))


(defn- get-all-ns-declarations
  [source-paths]
  (->> (map #(File. %) source-paths)
       (mapcat ns-find/find-ns-decls-in-dir)
       (map (juxt second parse/deps-from-ns-decl))))


(defn- add-deps [dep-graph depmap]
  (reduce (fn [ds [name dependencies]]
            (reduce (fn [g dep] (dep/depend g name dep)) ds dependencies))
          dep-graph depmap))


(defn get-dep-graph [source-paths]
  (->> (get-all-ns-declarations source-paths)
       (into {})
       (add-deps (dep/graph))))


(defn- unwind [m]
  (for [[k vs] m v vs] [k v]))


(defn- check-constraints [deps constraints]
  (apply merge-with conj {:failed [] :passed []}
        (for [[x y] (unwind constraints)]
          (if (dep/depends? deps x y)
            {:failed [x y]}
            {:passed [x y]}))))


(defn- dep-str [text x y c]
  (println (get-color c)
           text (str "[" x "] -> [" y "]")
           (get-color :reset)))


(defn- handle-results [{failed :failed passed :passed}]
  (doseq [[x y] passed] (dep-str "Constraint Passed: " x y :green))
  (doseq [[x y] failed] (dep-str "Constraint Violated: " x y :red))
  (when *exit-after-barrier*
    (System/exit (count failed))))


(defn barrier
  "Check code barriers"
  [project & args]
  (let [source-paths (:source-paths project)
        barriers (:barriers project)]
    (-> (get-dep-graph source-paths)
        (check-constraints barriers)
        (handle-results))))
