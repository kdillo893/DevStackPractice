# simplestFrontToBack
Creating a shell of an application, which includes a database connection, object structures, some API, and some front-end structure.

This is going to take a while, since I need to brainstorm what to do with it and also decide what technologies to use for practice.

## postgres setup
in order to have a postgres database and other things, need to download the client and set things up. Some arch instructions here: https://wiki.archlinux.org/title/PostgreSQL

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

## maven 
I'm mainly using maven to import and download jars for runtime rather than dealing with "finding a dependency around the web and downloading separately".

Maven can be used to install (package in a target directory) all the sources from my application and bundle them neatly.

To build the target directory from the project root, simply use

``mvn install``

To remove contents from the target directory and clear the directory, use

``mvn clean``

To package things that are currently within the target folder as a jar, use

``mvn package``

This puts things into a .war/.jar depending on the pom. This project uses WAR currently, and these will be used to deploy our servlets to Glassfish.

## web container setup
In order to have the servlets load, a web container directs traffic to the packaged set of classes which handle requests.
The web.xml is the deployment descriptor for which endpoints in the overall context direct to where.

### Glassfish
Install from https://glassfish.org/download.html .

Maven pom has information to point to the installation location for glassfish.
Right now I'm just running it from Netbeans and specifying the server to launch.

In order to configure the domain where we will launch things from, need to create that first.
create: (from glassfish installation) ```asadmin create-domain --adminport 4848 <domainName>```
this will prompt for admin username and password. The admin port will establish to run on 4848 and can be accessed from there.

starting and stopping the glassfish server:
Start: (from the installation location) ```asadmin start-domain <domainName>```
Shutdown: (from the installation location) ```asadmin stop-domain <domainName>```

adding the war to the domain for auto-deploy? hmmm... not sure.

deploying the war to the domain:
```asadmin deploy <thewarjar>```
```asadmin undeploy <thewarjar name without file extension>```

### Tomcat/TomEE
Google it...

Set envs to CATALINA_HOME to the installation location, JAVA_HOME to the java sdk base.

Starting and stopping from CATALINA_HOME/bin/startup.sh and CATALINA_HOME/bin/shutdown.sh

In order to deploy servlets to tomcat, need the resources to be compiled and placed in the tomcat home to replace the default servlet.
To do this, package the contents into a war/jar using maven package, then put the war into CATALINA_HOME/webapp.

Options to autoDeploy or deployOnStartup can make it easier to manage...

https://tomcat.apache.org/tomcat-10.1-doc/deployer-howto.html


## running


## old runtime
The runtime is currently operating with Servlets being handled through a web container. If I decide to go back to "run my own Http Handling garbage", the below will apply

### including dependencies with runtime
Taking notes from https://howtodoinjava.com/java-examples/set-classpath-command-line/ on some of the basics for running java applications from commandline without an IDE managing all those tasks.
When running from command line, you need to include the dependencies from external libraries (namely postgresql in this case) on the classpath. This can be done within IDE, but if I wanted to run the application OUTSIDE an IDE, I would need to know how to include various dependencies needed for runtime.

The way to run this is to include in the classpath all dependency jars or source directories (with the base being "where the classes are contained"). An example of the run with java in terminal on unix would be this:

``java -cp target/classes:$MVN_REPO/org/postgresql/postgresql/42.6.0/postgresql-42.6.0.jar com.kdillo.simple.SimpleApp``

This specifies that I will include in the classpath with the -cp option all the compiled classes from the "target" folder for my application and the postgresql jar and run the main method of the class ```SimpleApp```. Alternatively, you can set a path or classpath environment variable which would include all the dependencies and compiled application classes for the project. The way to do this in unix is:

```export CLASSPATH=[the stuff above for -cp option]```

If you have a directory with a set amount of jar or class files you wish to include, the classpath is capable of using wildcards. Classes tend to be referenced from the root of the directory where they are referenced from, while the jar needs to be explicitly included, or can be wild-carded with *.

```export CLASSPATH=extra/classes:extra/jars/*```


### running the main
In order to run the application, follow the above steps for adding required to the classpath, then run

`java com.kdillo.simple.SimpleApp`

If you want to run with other options for the java runtime, feel free.
