(ns flow.cursors
  (:require [flow.protocols :as fp]
            [flow.util :as u]
            [flow.dom :as fd]))

(defn cursor->el [quoted-deps quoted-cursor]
  (let [!el (atom nil)]
    (reify fp/DynamicElement
      (should-update? [_ updated-vars]
        (u/deps-updated? quoted-deps updated-vars))

      (build-element [_ state]
        (let [new-el (get state quoted-cursor)
              new-el (if (.-nodeType new-el)
                       new-el
                       (-> new-el
                           (str)
                           (js/document.createTextNode)))]
          
          (when-let [old-el @!el]
            (fd/swap-elem! old-el new-el))
          
          (reset! !el new-el)
          new-el))
      
      (handle-update! [this old-state new-state updated-vars]
        (fp/build-element this new-state)))))
