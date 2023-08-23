# simplestFrontToBack
Creating a shell of an application, which includes a database connection, object structures, some API, and some front-end structure.

This is going to take a while, since I need to brainstorm what to do with it and also decide what technologies to use for practice.

## postgres setup
in order to have a postgres database and other things, need to download the client and set things up.

1. download and install from latest here for the given platform: https://www.postgresql.org/download/
2. enable the services for postgresql and start them (in arch/manjaro, this is ```systemctl enable/start postgresql``` )
3. with postgres started, create the db. can either be with ```createdb [dbname]```. If unable to create because of no data directory location, point postgres to a new data directory with ```initdb -D path/to/db/directory```. and follow from scratch instructions.
4. in the codebase, I will use the postgresql jdbc (java database connector). This can be downloaded and added as a library or included in a pom.xml for maven and installed as a dependency.
5. for accessing the database from our jdbc connection, we need to add properties which will be read by driver manager when making a connection. This needs a URL, user, and password. Additional options can be added, as referenced from https://jdbc.postgresql.org/documentation/use/.
   1. user would be your db user or role. (can add as property with "user" or as parameter user)
   2. password is what you configured for that user/role (property "password" or )
   3. if using passwords across a network for database connections (not local), use the property "ssl" as "true".
   4. url will be specified in ```DriverManager.getConnection(url, properties);```; url is of a format described with the jdbc site. for local, using ```jdbc:postgresql://localhost:[port]/[dbname]?currentSchema=[schema]```.
6. asdf


For "from scratch" database settings, modify the following files within your postgres data directory (in my case, ```/var/lib/pgsql/data/```).
   1. modify the ```postgresql.conf``` file, either un-comment the line for "password_encryption" or write in ```password_encryption = scram-sha-256```  for the most secure option or ``md5`` for older databases. 
   2. modify the ```pg_hba.conf``` to use the given encryption type under the "TRUST" columns for the given space where you'll access the database from.
   3. from the postgres user (sudo -su postgres), run ```psql```, then create a new user and role with the desired name and password.
      1. example: ```CREATE USER mydbuser WITH PASSWORD 'testdb*123' LOGIN SUPERUSER CREATEDB;```; LOGIN, SUPERUSER and CREATEDB are privilege examples. for simple things and testing, can just use superuser and look for further configuration later.
   4. if you want to specify a role instead of a user, you can do the following:
      1. example ```CREATE ROLE my_db_role WITH PASSWORD 'testdb*123' LOGIN SUPERUSER CREATEDB;```
   5. exit from the postgres psql and user session and test logging in with your role/user with password using ```psql -U [role/user] -d [dbname]``` to see if your user can access the things specified.


## Other things
(still figuring out)