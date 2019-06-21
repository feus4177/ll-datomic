(ns harvested.core
  (:require [io.pedestal.http :as http]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [com.walmartlabs.lacinia.schema :as schema]
            [datomic.api :as d]
            ;;[harvested.conformity :as c])
            [io.rkn.conformity :as c])
  (:gen-class))

(def config (c/read-resource "config.edn"))

(def db-uri (str
              ((config :db) :domain)
              ":"
              ((config :db) :port)
              "/"
              ((config :db) :name)))

(def db-conn (d/connect db-uri))

(c/ensure-conforms db-conn (c/read-resource "datomic-schema.edn") [:harvested/migration-001])

(def hello-schema (schema/compile
                    {:queries {:hello
                               ;; String is quoted here; in EDN the quotation is not required
                               {:type 'String
                                :resolve (constantly "world")}}}))

(def service (lacinia/service-map hello-schema {:graphiql true}))

(defonce runnable-service (http/create-server service))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting the server...")
  (http/start runnable-service))
