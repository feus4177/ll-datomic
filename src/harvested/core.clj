(ns harvested.core
  (:require [clojure.instant :refer [read-instant-date]]
            [io.pedestal.http :as http]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [com.walmartlabs.lacinia.executor :as executor]
            [datomic.api :as d]
            [io.rkn.conformity :as c])
  (:import (java.util UUID)
           (java.text SimpleDateFormat))
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

(defn construct-pull
  [selections-tree]
  (mapv (fn [[selection [nested-definition]]]
          (let [alias-clause ((config :selection-lookup) selection)]
            (if (nil? nested-definition)
              alias-clause
              {alias-clause (construct-pull (nested-definition :selections))})))
        selections-tree))

(defn get-filters
  [args]
  (let [filters {:in [] :where [] :args []}]
    (if (nil? args)
      filters
      (cond-> filters
              (args :user)
              (->
                (update :in conj '?user-id)
                (update :args conj (args :user))
                (update :where into '[[?user :harvested/id ?user-id] [?entities :entry/user ?user]]))))))

(defn construct-query
  [context filters where]
  {:query {:find '[[(pull ?entities pull-expression) ...]]
           :in (into '[$ pull-expression] (filters :in))
           :where (into where (filters :where))}
   :args (into
           [(d/db db-conn)
            (construct-pull (executor/selections-tree context))]
           (filters :args))})

(defn query-users
  [context args _]
  (d/query (construct-query
             context
             (get-filters args)
             '[[?entities :user/email]])))

(defn query-entries
  [context args value]
  (println "-------------------")
  ;;(println "context:" context)
  (println "args:" args)
  (println "value:" value)
  (println "selections-tree:" (executor/selections-tree context))
  (println "======================")
  (d/query (construct-query
             context
             (get-filters args)
             '[[?entities :entry/user]])))

(def gql-schema (-> "gql-schema.edn"
                    c/read-resource
                    (util/attach-resolvers
                      {:queries/users query-users
                       :queries/entries query-entries})
                    (util/attach-scalar-transformers
                      {:scalars/parse-uuid #(UUID/fromString %)
                       :scalars/serialize-uuid str
                       :scalars/parse-instant read-instant-date
                       :scalars/serialize-instant #(.format (SimpleDateFormat. "yyyy-MM-dd'T'HH:mm:ssZ") %)})
                    schema/compile))

(def service (lacinia/service-map gql-schema {:graphiql true}))

(defonce runnable-service (http/create-server service))

(defn -main
  "I don't do a whole lot ... yet."
  [& _]
  (println "Starting the server...")
  (http/start runnable-service))
