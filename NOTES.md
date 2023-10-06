# Notes

Here are some things I'm using for reference and information to guide my development. 

## "Whole Web App" resources
Looking for some things for "how do I connect my front end and back end?"

Options:
* Servlets
* JSPs
* HttpServer (Apache, Tomcat, Nginx, etc...)
* Spring Web or MVC
* Hibernate
* JPA framework

https://www.digitalocean.com/community/tutorials/java-web-application-tutorial-for-beginners
Specific known examples from my past with Intuit/Itrios involve the servlet/jsp handling. (Apache, Tomcat, 
switched to Nginx and other things)

Known example from AppBind and similar places is api backend running with node js,
frontend with react on nodejs, and some tooling to load with that; node including the http or request dependency

Reading on https://developer.mozilla.org/en-US/docs/Learn/Server-side/First_steps/Web_frameworks for different 
web frameworks:

| Framework     | Language   | Webpage                                     |
|---------------|------------|---------------------------------------------| 
| Django        | Python     | https://www.djangoproject.com/              |
| Tomcat        | Java       | https://tomcat.apache.org/                  |
| Flask         | Python     | https://flask.palletsprojects.com/en/2.3.x/ |
| Express       | Node.js/JS | https://expressjs.com/                      |
| Deno          | JS         | https://deno.com/                           |
| Ruby on Rails | Ruby       | https://rubyonrails.org/                    |
| Laravel       | PHP        | https://laravel.com/                        |
| Spring        | Java       | https://spring.io/                          |

Testing around to understand things:

### Spring
Seems to hinge on a ```@SpringBootApplication``` annotation specifying the class of the main method and
running a ```SpringApplication``` loop with the context of the main class.

Any things set up with the annotation ```@RestController``` become handlers for the spring application and can
be called during the loop described above.

Specifics might be less clear...

### Tomcat/TomEE/Glassfish Web Containers
This uses Servlets and calls to JSP files to direct the server to perform actions and return stuff.

Need to have installed Apache Tomcat on the machine where the server is running and provide 
environment variables ```CATALINA_HOME``` and ```JAVA_HOME``` to communicate and route requests.

Instead of initializing an Http Server and configuring everything yourself (including listeners and handlers),
you can use a web container such as Tomcat/TomEE which will redirect the requests to a Servlet or return a given page.
In order for a web container like Glassfish/Tomcat to know "what you want to happen" with requests that are sent
to your server, a "Deployment Descriptor" like "webapp/WEB-INF/web.xml" will detail what servlets are mapped where,
which pages and resources are simply served without processing, and more. 

### Http Server with Handlers and Controllers
The more complicated manner of handling back end requests is to establish a server on your device
which will stay alive, listen to requests on a given port and configuration (domain/ip etc), and listen for
what is requesting the application at certain context levels to forward to handlers and controllers
for processing. This method of back end implementation spends a LOT of time to get everything running and
will be more difficult to port between machines.

## Json library analysis

Looking around to improve speed of Json, found a link to a github repository which tested some objects: https://github.com/fabienrenaud/java-json-benchmark

I had been using Gson or Jakarta Json because it was easiest, but it looks like for a simple project I should be using dsl-json or fastjson.

... IDK