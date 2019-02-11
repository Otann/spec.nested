(ns spec.nested-test
  (:require [clojure.test :refer :all]
            [spec.nested :refer :all]
            [clojure.spec.alpha :as s]))

(def-nested-keys ::not-nested
  {:int     int?
   :str     string?
   :opt/key keyword?})

(deftest not-nested

  (testing "spec for map without nesting"

    (is (true? (s/valid? ::not-nested {:int 1 :str "string"})))

    (is (false? (s/valid? ::not-nested {:int 1}))
        "missing keys is invalid")

    (is (true? (s/valid? ::not-nested {:int 1 :str "str" :foo "bar"}))
        "extra keys is still valid")

    (is (true? (s/valid? ::not-nested {:int 1 :str "str" :key :val}))
        "optional key present and matching is valid")

    (is (false? (s/valid? ::not-nested {:int 1 :str 0 :key "str"}))
        "optional key present but not matching is invalid"))

  (testing "subspecs are generated"

    (is (true? (s/valid? :spec.nested-test.not-nested/bar 1)))

    (is (true? (s/valid? :spec.nested-test.not-nested/bar 1)))))


(def-nested-keys ::one-level
  {:top {:bottom  int?
         :opt/key keyword?}})


(deftest one-level-nesting

  (testing "one-level nested map"

    (is (true? (s/valid? ::one-level {:top {:bottom 1}})))

    (is (true? (s/valid? ::one-level {:top {:bottom 1
                                            :key    :value}})))

    (is (false? (s/valid? ::one-level {:top {:key :value}}))
        "bottom level with missing required key is invalid")

    (is (false? (s/valid? ::one-level {:bottom 1}))
        "top level with missing required key is invalid"))

  (testing "subspecs for nested map"

    (is (true? (s/valid? :spec.nested-test.one-level/top
                         {:bottom 1})))

    (is (true? (s/valid? :spec.nested-test.one-level.top/bottom
                         1)))

    (is (true? (s/valid? :spec.nested-test.one-level.top/key
                         :val)))))
