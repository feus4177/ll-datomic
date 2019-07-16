Introduction
- Topic disservice: A lot more to Datomic that what is being presented, just showing developer perspective

Why?
- DBs were designed in a different context
    - Disk space was scarce: walk uphill both ways to compile code
    - long software development lifecycles
- Now that we are in a different context we can make different design choices

What are these new design choices?
- Immutability, "the past doesn't change"
- New architecture, storage-transactor-peer

vs. RDBs
- designed for the cloud
    - Sharding, read-replication, locking, etc. irrelevant
    - elastic
- minimal schema
- time is a first-class concept
- multi-value attributes

vs NoSQL
- ACID
- JOINS!!
- Sensible data structure

Demonstration
- minimal schema
  - only define attributes
  - not sure if I'm sold
- time provides built in auditing
- multi-valued attributes
- joins
- Think about facts not objects

Cool things
- query as data (easily generate nested queries, comparison to python optimization)

How does this apply to me?
- thinking about time
  - most applications have an audit log
  - few think about the implication of time on end users
- planning for accretion
- re-evaluate design decisions

Unsweetened Chocolate (and other things I didn't like)
- client vs. peer library
  - deceptively similar but not interchangeable
  - peer library can do a few extra things
  - most docs seem to assume client library which has a newer release
- Unable to reason about groups of attributes

Future Inquiries
- Performance of nested/recursive queries