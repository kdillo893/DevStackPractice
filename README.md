# [CRUD application](#crud-application)
Creating a small application that includes a database connection, object structures, some API, and some front-end structure.

 **WARNING:** I was making the setup way too complicated and using a bunch of different tools and dependencies. I want to rework this to be much simpler to set up. Setting up your own postgres DB from scratch is a pain, and there are tools to do that or public containers via docker to make the process easier. Another thing would be to have a bash script which does all the granular setup so that others who try to redo this repo won't get frustrated and give up (like me in the future)

## [Prerequisites](#prerequisites)

* Database -- using PostgreSQL. Create a "webapp" schema.
   * Follow setup in other PSQL_README.md or do it yourself
   * Ensure the database is running, create a schema for it with ``./src/main/db/initdb.sql``
   * Add a config file at ``./src/main/resources/config.properties`` with the following, change depending on your DB setup from prior steps:
      ```env title="config.properties"
      db.name=garbage
      db.schema=webapp
      db.host=localhost
      db.port=1234
      db.user=garbage
      db.password=garbage1234
      ```
* Web Container? -- instructions at [Web Container](#web-container-setup)
* Maven - for building and dependency management

## [Build](#build)

If you wanna clean it up, go ahead
```
mvn clean
```

Get all the dependencies for maven in the repo and package with maven
```
mvn install
mvn package
```

## [Running](#running)
Currently just using cargo, might fix in the future to have my own script to launch glassfish.
```
mvn cargo:run
```

## [Web Container Setup](#web-container-setup)
In order to have the servlets load, a web container directs traffic to the packaged set of classes that handle requests.
The web.xml is the deployment descriptor for which endpoints in the overall context direct to where.

### [Glassfish](#glassfish)
Install from https://glassfish.org/download.html .

``pom.xml`` has information to point to the installation location for glassfish.

In order to configure the domain where we will launch things from, need to create that first.
create: (from glassfish installation) 
```
asadmin create-domain --adminport 4848 <domainName>
```

this will prompt for admin username and password. The admin port will establish to run on 4848 and can be accessed from there.

starting and stopping the glassfish server:
Start: (from the installation location) 
```
asadmin start-domain <domainName>
```

Shutdown: (from the installation location) 
```
asadmin stop-domain <domainName>
```


adding the war to the domain for auto-deploy? Hmm... not sure.

deploying the war to the domain:

```
asadmin deploy <thewarjar>
```


```
asadmin undeploy <thewarjar name without file extension>
```


### [Tomcat/TomEE](#tomcat)
Install from: https://tomcat.apache.org/

Set environment variables to CATALINA_HOME to the installation location, and JAVA_HOME to the java sdk base.

Starting and stopping from CATALINA_HOME/bin/startup.sh and CATALINA_HOME/bin/shutdown.sh

In order to deploy servlets to Tomcat, need the resources to be compiled and placed in the Tomcat home to replace the default servlet.
To do this, package the contents into a war/jar using the maven package, then put the war into CATALINA_HOME/webapp.

Options to autoDeploy or deployOnStartup can make it easier to manage...

https://tomcat.apache.org/tomcat-10.1-doc/deployer-howto.html



## Some garbage
### old runtime instructions: PLEASE IGNORE
The runtime is currently operating with Servlets being handled through a web container. If I decide to go back to "run my own Http Handling garbage", the below will apply

#### including dependencies with runtime
Taking notes from https://howtodoinjava.com/java-examples/set-classpath-command-line/ on some of the basics for running Java applications from the command line without an IDE managing all those tasks.
When running from the command line, you need to include the dependencies from external libraries (namely Postgresql in this case) on the classpath. This can be done within IDE, but if I wanted to run the application OUTSIDE an IDE, I would need to know how to include various dependencies needed for runtime.

The way to run this is to include in the classpath all dependency jars or source directories (with the base being "where the classes are contained"). An example of the run with Java in a terminal on Unix would be this:

```
java -cp target/classes:$MVN_REPO/org/postgresql/postgresql/42.6.0/postgresql-42.6.0.jar com.kdillo.simple.SimpleApp
```

This specifies that I will include in the classpath with the -cp option all the compiled classes from the "target" folder for my application and the PostgreSQL jar and run the main method of the class 
``SimpleApp``
. Alternatively, you can set a path or classpath environment variable which would include all the dependencies and compiled application classes for the project. The way to do this in Unix is:

```
export CLASSPATH=[the stuff above for -cp option]
```

If you have a directory with a set amount of jar or class files you wish to include, the classpath is capable of using wildcards. Classes tend to be referenced from the root of the directory where they are referenced from, while the jar needs to be explicitly included, or can be wild-carded with *.


```
export CLASSPATH=extra/classes:extra/jars/*
```



#### Running the main
In order to run the application, follow the above steps for adding the required to the classpath, then run


```
java com.kdillo.simple.SimpleApp
```


