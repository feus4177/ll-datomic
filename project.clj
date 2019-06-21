(defproject harvested "0.1.0-SNAPSHOT"
  :description "A fictitious time-tracking app."
  :url "https://github.com/feus4177/ll-datomic"
  :license nil
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;;[com.datomic/client-pro "0.8.28"]
                 [com.datomic/datomic-pro "0.9.5786"]
                 [io.rkn/conformity "0.5.1"]
                 [io.pedestal/pedestal.service "0.5.5"]
                 [io.pedestal/pedestal.route "0.5.5"]
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 [org.slf4j/slf4j-simple "1.7.21"]
                 [com.walmartlabs/lacinia "0.33.0"]
                 [com.walmartlabs/lacinia-pedestal "0.12.0"]]
  :main ^:skip-aot harvested.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
