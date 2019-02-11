(ns spec.nested
  (:require [clojure.spec.alpha :as s]))

(defn- field-name [namespace-str name-keyword]
  (keyword namespace-str (name name-keyword)))


(defmacro def-nested-keys [spec-name structure]
  "Defined nested structure for keys in a map"
  (assert (map? structure) "Spec structure must be a map")
  (let [spec-ns#    (str (namespace spec-name) "." (name spec-name))

        ;; create a list of spec names for each field
        req-fields# (for [field# (filter #(not= "opt" (namespace %))
                                         (keys structure))]
                      (field-name spec-ns# field#))
        opt-fields# (for [field# (filter #(= "opt" (namespace %))
                                         (keys structure))]
                      (field-name spec-ns# field#))

        ;; create list of specs for each field
        specs#      (for [[k# v#] structure]
                      (let [field-spec# (field-name spec-ns# k#)]
                        (if (map? v#)
                          `(def-nested-keys ~field-spec# ~v#)
                          `(s/def ~field-spec# ~v#))))]
    `(do
       ;; define specs for each field
       ~@specs#

       ;; define top-level spec listing all fields as unqualified keys
       (s/def ~spec-name (s/keys :req-un ~(apply vector req-fields#)
                                 :opt-un ~(apply vector opt-fields#))))))
