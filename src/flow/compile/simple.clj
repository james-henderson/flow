(ns flow.compile.simple
  (:require [flow.compile :refer [compile-el compile-value-form]]
            [flow.util :as u]))

(alias 'fd (doto 'flow.dom create-ns))
(alias 'fp (doto 'flow.protocols create-ns))




(defmethod compile-el :primitive [{:keys [primitive elem?]} opts]
  {:el-return `(fd/->node ~primitive)})

(defmethod compile-value-form :primitive [{:keys [primitive elem?]} opts]
  {:inline-value primitive})





(defmethod compile-value-form :map [{m :map} opts]
  (let [flattened-map (flatten (seq m))
        compiled-elems (map #(compile-value-form % opts))]
    {:deps (set (mapcat :deps compiled-elems))
     :inline-value (->> compiled-elems
                        (map :inline-value)
                        (partition 2)
                        (into {}))}))





(defmethod compile-value-form :coll [{:keys [coll]} opts]
  (let [compiled-elems (map #(compile-value-form % opts) coll)]
    {:inline-value `(into ~(empty coll) [~@(map :inline-value compiled-elems)])
     :deps (set (mapcat :deps compiled-elems))}))




(defmethod compile-value-form :symbol [{:keys [sym]} {:keys [dynamic-syms local-syms state]}]
  (let [dynamic? (contains? dynamic-syms sym)
        local? (contains? local-syms sym)]
    {:deps (when dynamic?
             #{sym})
     :inline-value (if (or dynamic? local?)
                     `(get ~state (quote ~sym))
                     sym)}))

(defmethod compile-el :symbol [{:keys [sym path]} {:keys [dynamic-syms local-syms state updated-vars]}]
  (let [dynamic? (contains? dynamic-syms sym)
        local? (contains? local-syms sym)
        el (symbol path)]
    
    (cond
     dynamic? (let [deps #{sym}]
                {:deps deps
                 :el `(~el)
                 :declarations [`(defn ~el []
                                   (flow.symbol/symbol->el (quote ~sym)))]})

     local? {:el `(fd/->node (get ~state (quote ~sym)))}

     :else {:el `(fd/->node ~sym)})))
