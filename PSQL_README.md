# postgres setup
In order to have a Postgres database, there are several options.
* Use a hosted database service, (postgres has a list on their site here: [Hosted DBs](https://www.postgresql.org/support/professional_hosting/))
* Use a docker container and run it in a platform of your choice ([Docker hub link](https://hub.docker.com/_/postgres))
* Set it up manually on your own machine ([Download](https://www.postgresql.org/download/) and [Documentation on Installation/Setup](https://www.postgresql.org/docs/current/installation.html))

Below are some details on setting up postgresql locally on my machine (arch btw) and getting things to work how I wanted.

## local postgres setup (detailed)
Before going any further, ensure postgres is installed. On linux, you can do something like below:
```pacman -S postgresql```
Or download from their site here:

https://www.postgresql.org/download/

Some arch instructions here: https://wiki.archlinux.org/title/PostgreSQL

1. Download and install from the latest. 
2. Initialize your data directory in the desired location. Default is ```/var/lib/postgres/data```. This is done with running ``initdb`` under the postgres user.
   1. Ensure you have a postgres user and group defined. Then execute
      ```initdb -D '/your/data/directory/here]'```
   1. If you want your data directory somewhere else, the service file for postgresql needs to reflect that directory. An example is ``/home/postgres/data``, below would need to change
      * ```
      Environment=PGDATA=/home/postgres/data
      \[...\]
      ExecStartPre=/usr/bin/postgresql-check-db-dir ${PGDATA}
      ExecStart=/usr/bin/postgres -D ${PGDATA}
      \[...\]
      \# because of the directory being under "home"
      ProtectHome=false
      ```
3. Enable the PostgreSQL service and start (in arch/manjaro, this is ```systemctl enable/start postgresql``` )
4. Create the db with ```createdb [dbname]```. The ``dbname`` will be used for the project.

### configuration for basic password and authentication
Modify the following files within your Postgres data directory.
   1. Create a new user to manage this database.
      * run ``psql`` with postgres user and do something like below, replace user name and password to your liking.
      ```
      CREATE USER mydbuser WITH PASSWORD 'testdb*123' LOGIN SUPERUSER CREATEDB;
      ```
      LOGIN, SUPERUSER and CREATEDB are privilege examples. for simple things and testing, can just use superuser and look for further configuration later.
   2. ``postgresql.conf``

      * Add ``password_encryption`` with the desired encryption, like ``scram-sha-256`` or ``md5``. 
   3. ``pg_hba.conf``
      * Adjust the ``TRUST`` column to match your desired password encryption in step 1.
      *. if you want to specify a role instead of a user, you can do the following:
      ```
      CREATE ROLE my_db_role WITH PASSWORD 'testdb*123' LOGIN SUPERUSER CREATEDB;
      ```
   4. exit from the postgres psql and user session and test logging in with your role/user with password using ```psql -U [role/user] -d [dbname]``` to see if your user can access the things specified.

Still solving some permission issues, I had some issues when setting it up again.

## Connecting a codebase to a database
"How do I connect my code to some database and do things"?

Well that's easy: connect to it! There are plenty of libraries around used to connect to databases; most of them are wrappers around a socket connection. (I want to try creating my own db connector, that's a new project idea!)

I will use the Postgresql Java database connector ([JDBC](https://jdbc.postgresql.org/)).
This JDBC library needs a URL, user, and password. Additional options can be added, as referenced from https://jdbc.postgresql.org/documentation/use/.
   1. User would be your db user or role. (property "user")
   2. Password is what you configured for that user/role (property "password")
   3. If using passwords across a network for database connections (not local), use the property "ssl" as "true".
   4. URL will be specified in ```DriverManager.getConnection(url, properties);```; url is of a format described with the JDBC site. for local, using ```jdbc:postgresql://localhost:[port]/[dbname]?currentSchema=[schema]```.
