# TDD/CI/CD Walkthrough Facilitator's Guide

This project supports part 3 of a 3-part demonstration/walkthrough/dojo exercise. By the end of the series, you'll have a reusable jar containing the "Hello, World!" functionality, a standalone Java app, a RESTful microservice, and a working CI/CD pipeline.

* Part 1 - http://github.com/neopragma/javahellolib 
* Part 2 - http://github.com/neopragma/javahelloapp
* Part 3 - javahelloservice (You are here)

## 14. Purpose and overview

In the first two parts of the walkthrough, we touched on several activities that occur as part of a software delivery pipeline:

* Using Maven for Java projects
* Separation of concerns
* Using a version control system
* Single branch strategy
* Setting up continuous integration
* Using an IDE
* Packaging a reusable jar to be uploaded to a repository
* Test-driving application code through microtests
* Benefits of frequent commits
* Using Spring Boot with Maven 
* Packaging applications for deployment
* Setting up continuous deployment
* Writing a standalone Java application suitable for cloud deployment

In this (final) part, we'll touch on a couple more:

* Writing a RESTful microservice in Java suitable for cloud deployment
* Separating _unit_ and _integration_ tests in Maven


## 14. Write a RESTful service deployable as a war

Spring Boot is very handy for setting up common types of Java applications, such as standalone applications (as you did in part 2 of this walkthrough), conventional Web-based CRUD apps, and Web-based RESTful services.

### 14.1. Setting up the project

To get started, we'll create a repo on Github for the project, named ```javahelloservice```. Then we'll create a project root directory locally and set up the local Git repo for it.

Show participants how to set up a repo on Github, or show them the one you've already set up for purposes of this walkthrough. If they've gone through parts 1 and 2, they won't need to spend much time looking at this.

In the project root directory on the local development system, set up a local Git repo:

```shell
mkdir javahelloservice
cd javahelloservice 
echo '# javahelloservice' > README.md
echo '/target/' > .gitignore 
git init 
git config --global user.name "some name"
git config --global user.email "some email"
git add .
git commit -m "Initial commit"
git remote add origin [blah]
git push -u origin master
```

This is sufficient to set up the project. We can update the ```README.md``` and ```.gitignore``` files later. 


### 14.2. The POM

Now we'll create a Maven POM that declares the Spring Boot plugins and dependencies relevant to our goal.

```xml 
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.neopragma.javahelloservice</groupId>
    <artifactId>javahelloservice</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>war</packaging>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.4.2.RELEASE</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-rest</artifactId>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>    
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>   
 
</project>
```

Notice this POM has a parent POM named _spring-boot-starter-parent_, which declares components of Spring Boot that are helpful for creating and packaging Spring applications. We also see a dependency named ```spring-boot-starter-data-rest```, which helps with setting up an application that will be a REST service.

We've also overridden the default packaging and told Maven to package the artifact as a ```war```.


### 14.3. Maven directory structure

Let's create the basic directory structure for a Java Maven project.

```shell
mkdir -p src/main/java
mkdir -p src/test/java
```

### 14.4. Import the project into Spring Tool Suite

We could have done all this from inside the IDE, but it's quicker and easier to do it on a command line. Let's bring the project into the IDE now. Just use the same procedure as we did previously.

### 14.5. What Spring Boot provides

An oddity about Spring is that you must have a ```main``` method, even if your application doesn't run standalone. The class for the service is straightforward. As a basic starting point, we only need this:

```java 
package com.neopragma.javahelloservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
 
@SpringBootApplication
public class Application {
 
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

This is where the value of Spring Boot becomes apparent. For the moment, let's just drop this boilerplate code into the project and see what it can do even before we've added any application-specific logic to it.

We'll hope that participants feel uncomfortable about putting code into ```src/main/java``` without having first written test code in ```src/test/java```. It should feel _normal_ to write tests first, and _abnormal_ to write production code first. That way, when we _choose_ not to write tests first, we'll think about it carefully and be sure that we have good reasons.

Before we do anything else, we can run

```shell 
mvn spring-boot:run
```

On the author's system, on the first try, this was (part of) the output from that command:

```shell 
Daves-MacBook-Pro:javahelloservice dave$ mvn spring-boot:run
[INFO] Scanning for projects...
[INFO]                                                                         
[INFO] ------------------------------------------------------------------------
[INFO] Building javahelloservice 0.0.1-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] >>> spring-boot-maven-plugin:1.4.2.RELEASE:run (default-cli) > test-compile @ javahelloservice >>>

(more Maven build output here, removed to save space)

[INFO] 
[INFO] --- spring-boot-maven-plugin:1.4.2.RELEASE:run (default-cli) @ javahelloservice ---

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v1.4.2.RELEASE)

2016-11-19 07:01:35.358  INFO 2730 --- [           main] c.n.javahelloservice.Application         : Starting Application on Daves-MacBook-Pro.local with PID 2730 (/Users/dave/Documents/Projects/javahelloservice/target/classes started by dave in /Users/dave/Documents/Projects/javahelloservice)
2016-11-19 07:01:35.362  INFO 2730 --- [           main] c.n.javahelloservice.Application         : No active profile set, falling back to default profiles: default

(more Spring Boot output here, removed to save space)

(notice this: it's automatically defining RESTful routes)

2016-11-19 07:01:39.127  INFO 2730 --- [           main] o.s.d.r.w.RepositoryRestHandlerMapping   : Mapped "{[/{repository}/{id}],methods=[PATCH],produces=[application/hal+json || application/json || application/*+json;charset=UTF-8]}" onto public org.springframework.http.ResponseEntity<org.springframework.hateoas.ResourceSupport> 

(more output like the above, removed to save space)

2016-11-19 07:01:39.143  INFO 2730 --- [           main] o.s.d.r.w.BasePathAwareHandlerMapping    : Mapped "{[/profile/{repository}],methods=[GET],produces=[application/schema+json]}" onto public org.springframework.http.HttpEntity<org.springframework.data.rest.webmvc.json.JsonSchema> org.springframework.data.rest.webmvc.RepositorySchemaController.schema(org.springframework.data.rest.webmvc.RootResourceInformation)
2016-11-19 07:01:39.166  INFO 2730 --- [           main] .m.m.a.ExceptionHandlerExceptionResolver : Detected @ExceptionHandler methods in repositoryRestExceptionHandler
2016-11-19 07:01:39.309  INFO 2730 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2016-11-19 07:01:39.417  INFO 2730 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2016-11-19 07:01:39.422  INFO 2730 --- [           main] c.n.javahelloservice.Application         : Started Application in 5.144 seconds (JVM running for 10.082)
                    
```

There's an embedded Tomcat server running on ```localhost:8080```. If we open a second command prompt, we can interact with the bare-bones default REST service.

Spring Boot creates a default query named ```_links``` that returns all the RESTful routes available. Let's use ```curl``` to invoke it, and see what it returns:

```shell 
curl localhost:8080
```

It returns:

```shell 
{ "_links" : { 
  "profile" : { 
    "href" : "http://localhost:8080/profile" }
  }
}
```

So far, the application doesn't do anything useful, but the structure is in place and it runs. All this comes for free from Spring Boot. 


### 14.6. Where does TDD fit in?

It's a generally-accepted good practice to test-drive all code that we write ourselves. So far, we've only used functionality provided out-of-the-box by Spring Boot. We may assume someone else has already ensured that code works correctly. Even if that assumption is optimistic, we can _safely_ assume that some one else is _responsible_ for ensuring that code works correctly. _We_ are responsible for the code _we_ write.

Our application needs to pull in the library jar we created in part 1 of the walkthrough, named ```javahellolib```. Then the ```main``` method of the service needs to call the ```greet``` method of the ```Hello``` class. 

Spring Boot does not automatically provide that logic, and neither does any other framework or tool. As a question of good practice, then, we should test-drive that part of the code.


### 14.7. Using test doubles

Remind participants of the canonical test automation pyramid. When we test-drive code, we're working at the "unit" level of the pyramid, writing _microtests_. The _scope_ of a microtest for Java code is a single path through a single method. 

A rule of thumb for any automated test case is that the case should be able to fail for exactly one reason: That the code under test doesn't behave as expected. Test cases should _not_ be vulnerable to failure because of database corruption, network resource availability, a change in input data, or a bug in a collaborator. 

Our ```Hello``` class is a collaborator of our service application. In our microtest, we want to be sure the service calls the ```greet``` method, but we don't want to be vulnerable to a failure in case the ```greet``` method has a bug. We've already checked that in the microtests for the ```Hello``` class. We'll put the two pieces together in an _integration test_ shortly, but that is _not_ the job of a microtest.

To mask off collaborators in test code, we use _test doubles_ to stand in for the real collaborators. Two types of test doubles are commonly used:

* **stub** - exhibits lowest-common-denominator behavior only; basically does "nothing"
* **mock** - presents the same API as the real thing and returns a set value defined by the test case; can also count the number of times it's called during the test.

You can roll your own test doubles or you can use a library to handle it for you. Several libraries are available for the Java language. We're going to use one called Mockito for this walkthrough.

### 14.8. Adding Mockito to the project

Maven needs to know about Mockito, so we'll declare it as a dependency. We're only using it at _test_ scope, so we'll specify that in the declaration.

```xml 
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-all</artifactId>
    <version>1.9.5</version>
    <scope>test</scope>
</dependency>
```

We'll suggest adding this right under the JUnit dependency, as they both pertain to unit testing. Ordering the declarations in a logical way helps people understand the POM. POM files tend to become large, and anything we can do to help our colleagues deal with them will be appreciated.


### 14.9. Test-driving the "Hello" functionality in the service

Now that our testing and mocking resources are configured, let's assert the behavior we want to see. 

Our RESTful service will need to have a Spring Rest controller class. That will be the object that calls our ```Hello``` functionality, so that's the class we'll test-drive. We'll treat the boilerplate ```Application``` class as a free gift, courtesy of Spring Boot.

This isn't the greatest example for TDD, as most of the work is done for us by the Spring framework. The only application logic we need to hand-code is the Spring Bean configuration and the call to the ```greet``` method of class ```Hello```. 

The former is boilerplate. It looks like this:

```java 
package com.neopragma.javahelloservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.neopragma.javahellolib.Hello;

@Configuration
public class AppConfig {
    @Bean
    public Hello hello() {
        return new Hello();
    }
}
```

For the latter, we can use a _mock_ to check that the call is made without making our test case vulnerable to possible bugs in the underlying ```Hello``` class. 

First, we need to declare ```javahellolib``` as a dependency in the ```javahelloservice``` project.

```xml 
<dependency>
    <groupId>com.neopragma</groupId>
    <artifactId>javahellolib</artifactId>
    <version>1.0.0</version>
</dependency>
```

Now we assert the behavior we want to see. We'll just present a finished test class, as there are several new concepts at play.

```java 
import static org.mockito.Mockito.verify;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.neopragma.javahellolib.Hello;

@RunWith(MockitoJUnitRunner.class)
public class HelloControllerTest {
	
	@Mock Hello hello;
	
	@Test
	public void itRetrievesTheGreeting() {
		HelloController controller = new HelloController(hello);
		controller.greeting();
		verify(hello).greet();
	}
}
```

Here's what's going on. There are several imports and annotations for library functionality we want to use. 

Mockito uses a custom runner to work with JUnit. To specify that, we need the ```@RunWith``` annotation provided by JUnit as well as the ```MockitoJUnitRunner``` class provided by Mockito. 

We also need the usual ```@Test``` annotation so JUnit will recognize which methods are meant to be test cases, as well as the ```@Mock``` annotation so Mockito will recognize which object references are meant to be mocks.

Our test setup assumes there will be a class named ```HelloController``, which will be a Spring REST controller that takes an injected instance of ```Hello```.

When the Spring framework calls the ```greeting``` method on ```HelloController```, we _verify_ that the ```greeting``` method calls the ```greet``` method of ```Hello``` once. (_Once_ is the default for ```verify```, so it isn't specified.)

When the test fails for the right reason, we make it pass. You've seen this done step by step before, so we'll just present the resulting ```HelloController``` source.

```java 
package com.neopragma.javahelloservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.neopragma.javahellolib.Hello;

@RestController
public class HelloController {	
  	private Hello hello;	
  	public HelloController(Hello hello) {
  		this.hello = hello;
  	} 
    @RequestMapping(method=RequestMethod.GET, value={"/greeting"})
    public String greeting() {
        return hello.greet();
    }
}
```

Now we rebuild:

```shell 
mvn clean package
```

and restart our local Tomcat server via Spring Boot

```shell 
mvn spring-boot:run
```

and try our ```curl``` command again

```shell 
curl localhost:8080/greeting
```

and we see the familiar "Hello, World!" text appear on the console.


## 15. Integration tests

We rushed through that last bit because you've seen similar steps before, and we want to move on to something new: Integration tests.

So far, we haven't written any automated tests or checks beyond the _unit_ or _microtest_ level. It would be prudent to run integration tests before promoting our application to production. Let's see how to include that level of testing in our Maven build.

We can tell Maven when to run specific types of tests by using the ```surefire``` and ```failsafe``` plugins to control which test classes are associated with which Maven goals.

We can use the ```@Category``` interface in JUnit to group specific test classes into whatever sort of categories make sense for our application. 

Using these two features together, we can control when the unit tests run and when the integration tests run.

Let's begin by creating an integration test class and marking it with the ```@Category``` annotation. First, we need an interface to identify integration test classes.

```java 
package com.neopragma.javahelloservice;

public interface IntegrationTest() {}
``` 

Now we can create an integration test class and mark it using the ```@Category``` annotation. 

```java 
package com.neopragma.javahelloservice;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.neopragma.javahellolib.Hello;

@Category(IntegrationTest.class)
public class HelloControllerIT {
	
	@Test
	public void itReturnsHelloWorld() {
		HelloController controller = new HelloController(new Hello());
		assertEquals("Hello, World!", controller.greeting());
	}
}
```

This isn't the most interesting test class ever written. It's pretty much the same as the microtest, except this time we're passing the _real_ ```Hello``` to the constructor of ```HelloController```.

The goal of the test case is to ensure that the correct text is returned by the controller after it retrieves the value from the ```Hello``` class. 

The scope of this test lies between the _microtest_ level and the _end-to-end functional_ level. We're checking that the two classes, ```HelloController``` and ```Hello```, are connected properly. That's what an integration test is for: To see that two components are connected properly. That's all. 

We can run this inside the IDE using Run as -> JUnit test to be sure we've coded it correctly. 

Now we need to tell Maven when to run this test. The ```surefire``` plugin controls the execution of _unit_ tests, and the ```failsafe``` plugin controls the execution of _integration_ tests. We'll tell the ```surefire``` plugin to _exclude_ classes marked as ```IntegrationTest```, and we'll tell the ```failsafe``` plugin to _include_ them.

```xml 
<plugin>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>2.18.1</version>
  <configuration>
    <excludedGroups>com.neopragma.javahelloservice.IntegrationTest</excludedGroups>
  </configuration>
</plugin>
<plugin>
  <artifactId>maven-failsafe-plugin</artifactId>
  <version>2.18.1</version>
  <configuration>
    <includes>
      <include>**/*.java</include>
    </includes>
    <groups>com.neopragma.javahelloservice.IntegrationTest</groups>
  </configuration>
  <executions>
    <execution>
      <goals>
        <goal>integration-test</goal>
        <goal>verify</goal>
      </goals>
    </execution>
  </executions> 
</plugin>
```

Let's try it and see what happens.

When we run

```shell 
mvn test
```

we see that Maven ran the unit test:

```shell 
Running com.neopragma.javahelloservice.HelloControllerTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.012 sec - in com.neopragma.javahelloservice.HelloControllerTest

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

When we run

```shell 
mvn verify
```

we see that Maven separately ran the unit test and then the integration test:

```shell 
Running com.neopragma.javahelloservice.HelloControllerIT
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 sec - in com.neopragma.javahelloservice.HelloControllerIT

Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```


## 16. Setting up continuous integration and continuous deployment

So far we've been working locally for this part of the walkthrough. Let's set up our continuous integration on Travis CI with continuous delivery to Heroku, as we did for ```javahelloapp```.

On http://travis-ci.org we flip the switch on our Github repository for ```javahelloservice```. Then we go to http://heroku.com and create an app named ```javahelloservice```.

Now we create a ```.travis.yml``` file for the ```javahelloservice``` project with the usual base content for a Java 8 app:

```yml 
language: java
jdk:
- oraclejdk8
```

Next, we use the Travis and Heroku command line tools to create an encrypted API key for Heroku, like this:

```shell  
travis encrypt $(heroku auth:token) --add deploy.api_key
```

We see that it added the ```deploy``` section to our ```.travis.yml``` file, but it didn't add a ```provider``` key. After adding that manually, we have:

```shell
language: java
jdk:
- oraclejdk8
deploy:
  provider: heroku
  api_key:
    secure: [a long encrypted value appears here]
```

Now when you push to Github, you should see a Travis CI build start. It will attempt to deploy to Heroku, but will get an error because our ```javahellolib``` jar isn't present in Maven Central. The general CI/CD process is working, however. 


## 17. Tools and more tools 

In going through this walkthrough, you may have noticed a large quantity of "stuff" above and beyond the application code. Resources and artifacts and scaffolding and external servers and test cases and configuration files and scripts...the list goes on. The quantity of code downloaded as dependencies was huge, compared with the amount of code in the "Hello, World!" application itself. There's only one microtest in this example, but it's common to see 5 to 10 times as much unit test code as production code in a "real" application. 

All of this is pretty normal, but we often don't notice it or think much of it. It goes to show how much tools like Maven, Spring Boot, Travis CI, and Heroku help us. We can focus our efforts on value-add application logic, while leaving most of the scaffolding to tools and services.


## 18. Whose job is it, anyway? 

Programmers may be accustomed to leaving most of these responsibilities to others - writing test cases, configuring the build script, defining projects in version control systems, setting up the CI server, managing automated deployment, preparing artifacts to be uploaded to repositories, provisioning environments, and so forth. Programmers like to be called "developers" or "engineers," but if all they do is sling code...?

As we direct our career growth toward being _developers_ as opposed to _coders_, we'll be dealing with more and more of these things ourselves, rather than depending on specialized teams to do various bits and pieces for us. What of the people on those specialized teams? Well, they'll become _developers_, too. They'll be learning how to program, along with all the other development skills. As the saying goes: It's all rock and roll.


## 19. Where do we stand?

We've touch on quite a few good practices for software development and delivery, although we haven't taken a deep dive into any of them. Feel free to follow up and do so, as your interests guide you.

* Using Maven for Java projects
* Separation of concerns
* Using a version control system
* Single branch strategy
* Setting up continuous integration
* Using an IDE
* Packaging a reusable jar to be uploaded to a repository
* Test-driving application code through microtests
* Benefits of frequent commits
* Using Spring Boot with Maven 
* Packaging applications for deployment
* Setting up continuous deployment
* Writing a standalone Java application suitable for cloud deployment
* Writing a RESTful microservice in Java suitable for cloud deployment
* Separating _unit_ and _integration_ tests in Maven
