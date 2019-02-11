# Nested definitions for `clojure.spec`

It is often needed to define a spec for a very nested map,
but `clojure.spec` forces you to define a separate spec for each key.

While still complying with it's philosophy we can use more readable definition
with some guidelines applied to naming our specs:

```
(ns a.b.c
  (:require [[spec.nested :refer [def-nested-keys]]))

(def-nested-keys ::nested {:foo {:bar int?
                                 :baz string?}})

```

This macro expands to the following list of definitions:

Top-level keyword is used to define `s/keys` spec

```
(s/def :a.b.c.nested
  (s/keys :req-un [:a.b.c.nested/foo]))
```

And then it's name used to define a sub-namespace, where spec for each field would be defined:

```
(s/def :a.b.c.nested/foo
  (s/keys :req-un [:a.b.c.nested.foo/bar
                   :a.b.c.nested.foo/baz]))
```

And so on recursively:

```
(s/def :a.b.c.nested.foo/bar int?)
(s/def :a.b.c.nested.foo/baz string?)
```

## Optional and Required

By default mentioned keys in a map are required, but you can make them optional, by using `opt` namespace:

```
(def-nested-keys ::node {:number int?
                         :opt/string string?})
```

## Aliases to existing specs

You can use any specs in a map, only maps are expanded recursively.
For instance, if you want to reuse field, defined somewhere else as a spec:

```
;; spec defined somewhere else
(s/def ::node_id string?)

;; could be reused
(def-nested-keys ::node {:id       ::node_id
                         :children (s/+ ::node_id)})
```

## License

Copyright © 2019 Anton Chebotaev

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
