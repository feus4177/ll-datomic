(ns harvested.migrations.003
  (:require [datomic.api :as d])
  (:import (java.util UUID)))

(defn run [db-conn]
  (let [eids (map first (d/q '[:find ?e :where (or [?e :user/email]
                                                   [?e :entry/user])]
                             (d/db db-conn)))]
    [(for [eid eids]
       [:db/add eid :harvested/id (UUID/randomUUID)])]))