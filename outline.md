Introduction
- Topic disservice: A lot more to Datomic that what is being presented, just showing developer perspective

Why?
- DBs were designed in a different context
    - Disk space was scarce: walk uphill both ways to compile code
    - long software development lifecycles
- Now that we are in a different context we can make different design choices

What are these new design choices?
- Immutability, "the past doesn't change"
-

vs. RDBs
- designed for the cloud
    - Sharding, read-replication, locking, etc. irrelevant
    - elastic
- minimal schema
- time is a first-class concept

vs NoSQL
- ACID
- JOINS!!
- Sensible data structure