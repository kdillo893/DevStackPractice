# What am I doing with DB classes and functionality?

I've read around about "database access objects" and stuff from other Java tutorials as well as controllers, implementations, and the like. How would I think of implementing things?

This one YT video describes separating out the data layer from the business logic (services and servlets and logic for data usage on one segment, DAO and Object and Controller/Impl on another).

Found a document here which reminds me of some things in previous business apps: https://www.javaguides.net/2018/08/data-access-object-pattern-in-java.html

## Classes

Per-table, should create a set of classes referring to data rows.

Example would be User for the users table:
1. User = class which holds data used by business logic; these will be in com.kdillo.simple.entities
2. DataAccessObject = Use this to as gateway between business logic and database logic
3. UserDBImpl = contains the DB connection mechanisms for the .... why do I need to do this?
