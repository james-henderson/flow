(defproject jarohen/flow.todomvc ""

  :description "A sample ToDoMVC app demonstrating Flow"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  
  :dependencies [[org.clojure/clojure "1.6.0"]

                 [ring/ring-core "1.2.0"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.4"]

                 [prismatic/dommy "0.1.2"]

                 [org.clojure/core.async "0.1.301.0-deb34a-alpha"]
                 [org.clojure/clojurescript "0.0-2202"]

                 [gaka "0.3.0"]

                 [jarohen/flow "0.1.0-SNAPSHOT"]]

  :plugins [[jarohen/lein-frodo "0.3.0-rc2"]
            [lein-cljsbuild "1.0.3"]
            [lein-pdo "0.1.1"]]

  :frodo/config-resource "todomvc-config.edn"

  :resource-paths ["resources" "target/resources"]

  :cljsbuild {:builds {:dev
                       {:source-paths ["src"]
                        :compiler {:output-to "target/resources/js/todomvc.js"
                                   :output-dir "target/resources/js/"
                                   :optimizations :whitespace
                                   :pretty-print true}}}}

  :aliases {"dev" ["pdo" "cljsbuild" "auto" "dev," "frodo"]})
