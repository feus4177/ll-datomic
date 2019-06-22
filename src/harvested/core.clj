(ns harvested.core
  (:require [io.pedestal.http :as http]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [datomic.api :as d]
            [io.rkn.conformity :as c])
  (:import (java.util UUID))
  (:gen-class))

(def config (c/read-resource "config.edn"))

(def db-uri (str
              ((config :db) :domain)
              ":"
              ((config :db) :port)
              "/"
              ((config :db) :name)))

(def db-conn (d/connect db-uri))

(c/ensure-conforms db-conn (c/read-resource "datomic-schema.edn") [:harvested/migration-003])

(defn query-users
  [_ _ _]
  (d/q '[:find [(pull ?entities [[:harvested/id :as :id]
                                 [:user/email :as :email]]) ...]
         :where [?entities :user/email]]
       (d/db db-conn)))

(def gql-schema (-> "gql-schema.edn"
                    c/read-resource
                    (util/attach-resolvers {:queries/users query-users})
                    (util/attach-scalar-transformers {:scalars/parse-uuid #(UUID/fromString %)
                                                      :scalars/serialize-uuid str})
                    schema/compile))

(def service (lacinia/service-map gql-schema {:graphiql true}))

(defonce runnable-service (http/create-server service))

(defn -main
  "I don't do a whole lot ... yet."
  [& _]
  (println "Starting the server...")
  (http/start runnable-service))
