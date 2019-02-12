# Get your Spring apps ready for Kubernetes using Spring Actuator and Liberty Boost

## Overview

Spring Boot Actuator provides integrated support for core capabilities that an application needs to run in a Kubernetes environment, like a health endpoint for readiness, and a metrics endpoint for telemetry. The API and behavior of Spring Boot actuator has also changed between Spring Boot 1 and Spring Boot 2.

Open Liberty is an open source runtime designed to run cloud native Java applications, including Spring Boot applications, efficiently. Liberty Boost provides Maven and Gradle plugins that can build a layered Docker image for your Spring Boot application that will cache well for iterative development and rolling deployments.

In 15 minutes you will see how Spring Boot Actuator has changed between version 1 and version 2. We'll customize and extend the behavior of key endpoints for Kubernetes environments, and we'll use Liberty Boost to pack the Spring Boot application into a compact Docker image using Open Liberty, Open JDK, and Open J9.

## Agenda

- [Setting up](#setting-up)
- [Spring Actuator](#spring-actuator)
  - [Health Endpoint](#health-check)
  - [Custom Endpoint](#custom-actuator)
- [Liberty Boost](#liberty-boost)
- [Metrics](#metrics)
  - [Prometheus](#prometheus)

## Setting Up

This Lab gives you a small sample Spring Application to experiment with. Allowing us to explore
Actuators and Liberty Boost without needing to spend too long creating projects etc.

To get started, clone this repository.. 

```console
git clone https://github.com/ibm/spring-alarm-application.git
cd spring-alarm-application
```

Now we can build it to test the application =) 

_Note:_ Follow these steps anytime during this Lab when you want to try out changes you have made to the application.


```console
mvn clean package
```

And then run it.. 

```console
mvn spring-boot:run
```

And check it works by opening a browser at http://127.0.0.1:9099/web/

That's it! you're all set to explore Actuators & Liberty Boost.

> **Note:** The default branch of the repository uses Spring Boot 2.x, but we'll be talking about 
> both Boot 1.x & Boot 2.x during the Lab, they [each have](https://github.com/IBM/spring-alarm-application/tree/spring-boot-1) [their own](https://github.com/IBM/spring-alarm-application/tree/spring-boot-2) branch in the repository! 

## Spring Actuator

Spring Boot Actuator has been part of Spring Boot since the beginning of Spring Boot. Actuator has
the goal of bringing "production ready" features to a Spring application. "Production ready" 
encompasses many capabilities that are expected of modern Cloud Native applications,
including Health Checks, Metrics, and Configuration. 

First lets take a closer look at Spring's Health Check support.. 

## Health check 

The alarm application has a health check endpoint available at

- Boot 1.x - `/health`
- Boot 2.x - `/actuator/health`

The endpoint in is supplied via Spring Actuator. Lets see how it's enabled 
in this application. 

Actuators are enabled via this dependency in [pom.xml](https://github.com/IBM/spring-alarm-application/blob/master/pom.xml#L22-L25)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

The endpoint is made accessible in [application.properties](https://github.com/IBM/spring-alarm-application/blob/master/src/main/resources/application.properties#L8-L9)

- Boot 1.x - `management.security.enabled`
  - Not really specific to the health check endpoint, but if we don't set this to false here
    then we'd have to allow access to the health endpoint explicitly in the web security
    config. Actuator security in Boot 1.x is a little entertaining. Even though the application
    is configured to deny all access to non permitted paths, the actuators are still reachable.
    If this property is set to true however, we will have to permit the health endpoint as 
    a path as part of the web security config. Although if web security isn't enabled for the 
    application, then `/health` will be accessible regardless of the setting of this property. 
    (But it's content will only contain _sensitive_ content if this property is false, or 
     if you are authenticated). It's not the most straightforward configuration!  
     
- Boot 2.x - `management.endpoints.web.exposure.include` must include `health`
  - This property is a comma separated list of which endpoints should be exposed over http.

If required, the endpoint is also allowed through the web security configuration in [SecurityConfig.java](https://github.com/IBM/spring-alarm-application/blob/master/src/main/java/com/example/spring/actuatorsdemo/config/SecurityConfig.java#L23-L25)

- Boot 1.x - Not present. 
  - When `management.security.enabled` is false, actuator endpoints are accessible regardless
    of application security configuration.
- Boot 2.x
  - As part of the `WebSecurityConfigurerAdapter`s configuration of the http security, 
    - `.requestMatchers(EndpointRequest.to("health").permitAll()`

Spring's health check endpoint is contributed to by `HealthIndicator`s. Spring derives the final value
for the health check by aggregating the HealthIndicators it finds, which are supplied by the various 
Spring dependencies you may have added to the application. In addition, you can create your own beans
that implement the `HealthIndicator` interface, and they will be taken into account also. 

For more information, check the [Spring Documentation for Health Information](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-health)

> **Note:** although it is very simple to author your own REST endpoint for health checks in Spring, doing so means you 
> lose this aggregation behavior, and may miss out on application health issues that Spring is aware of.
  

> **LAB:** Visit the health check endpoint in your locally running application at http://127.0.0.1:9099/actuator/health 
> Try removing the `health` entry from either the `SecurityConfig.java` or `application.properties` entries, (and rebuild & rerun) to see what happens if you forget this part of the configuration.

## Custom actuator

The Spring Actuator framework in Boot 2.x was given a significant overhaul, providing an infrastructure for hosting endpoints that's entirely independent of Spring MVC / WebFlux / Jersey etc. For more information, check the [Spring Blog Post on Actuator Endpoints in Boot 2.x](https://spring.io/blog/2017/08/22/introducing-actuator-endpoints-in-spring-boot-2-0)

Both Boot 1.x & Boot 2.x offer support for custom actuators, but Boot 2.x's is significantly simpler. 

- Boot 1.x (see [example](https://github.com/IBM/spring-alarm-application/blob/spring-boot-1/src/main/java/com/example/spring/actuatorsdemo/ActuatorsdemoApplication.java#L28-L50))
```java
@Component
public class LivenessEndpoint implements Endpoint<String> {
    
    @Override
    public String getId() {
        return "liveness";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return false;
    }

    @Override
    public String invoke() {
        return "{\"status\":\"UP\"}";
    }
}
```
- Boot 2.x (see [example](https://github.com/IBM/spring-alarm-application/blob/spring-boot-2/src/main/java/com/example/spring/actuatorsdemo/ActuatorsdemoApplication.java#L29-L37))
```java
@Endpoint(id = "liveness")
@Component
public class Liveness {
    @ReadOperation
    public String testLiveness() {
        return "{\"status\":\"UP\"}";
    }
}
```

Just as with the health check endpoint, you have to allow access to the custom endpoint via [SecurityConfig.java](https://github.com/IBM/spring-alarm-application/blob/master/src/main/java/com/example/spring/actuatorsdemo/config/SecurityConfig.java#L23-L25) and [application.properties](https://github.com/IBM/spring-alarm-application/blob/master/src/main/resources/application.properties#L8-L9). Remember to use the name of the custom endpoint!

> **LAB:** Add a new custom endpoint, and test you can invoke it!!



## Liberty Boost

OpenLiberty Boost is an incredibly easy way to create a docker container
that will run your Spring application using OpenLiberty and OpenJ9.

Using it is as easy as adding 

```xml
<plugin>
    <groupId>io.openliberty.boost</groupId>
    <artifactId>boost-maven-plugin</artifactId>
    <version>0.1</version>
</plugin>
```

to your `pom.xml` plugins section. ([example](https://github.com/IBM/spring-alarm-application/blob/spring-boot-2/pom.xml#L77-L81))

And then, you can easily build a docker image with your Spring app by 
doing.. 

`mvn boost:docker-build`

Which will use a multi-layer docker build to assemble an image to run the Spring application. 

For more information, check the [Open Liberty Blog Post on using the boost-maven-plugin](https://openliberty.io/blog/2018/09/12/build-and-push-spring-boot-docker-images.html)

> **LAB:** try building a docker image for the application using the command above. Inspect details on built image using `docker image inspect actuatorsdemo:latest` or try running the image using `docker run -p 9099:9080 actuatorsdemo:latest`

## Spring metrics

Spring Metrics are also part of the Spring Actuator, and are enabled by this dependency in `pom.xml`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

The Spring metrics endpoint is not accessible by default, to expose it, you have to set 
some properties in `application.properties`

- Boot 1.x
  - `endpoints.metrics.sensitive` must be set to `false` to allow access to the metrics endpoint.
- Boot 2.x - `management.endpoints.web.exposure.include` must include `metrics`
  - This property is a comma separated list of which endpoints should be exposed over http.
  - eg. _ `management.endpoints.web.exposure.include=health,metrics`_

If required, the endpoint is also allowed through the web security configuration in `SecurityConfig.java`

- Boot 1.x - Not present. 
  - When `management.security.enabled` is false, actuator endpoints are accessible regardless
    of application security configuration.
- Boot 2.x
  - As part of the `WebSecurityConfigurerAdapter`s configuration of the http security, 
    - `.requestMatchers(EndpointRequest.to("metrics").permitAll()`
    - _the `.to(...)` method can accept multiple args, eg `to("health","metrics")`_

The endpoint is then reachable at

- Boot 1.x - `/metrics`
- Boot 2.x - `/actuator/metrics`

In this application there are [Gauges](https://micrometer.io/docs/concepts#_gauges) declared in the [AlarmMetrics](https://github.com/IBM/spring-alarm-application/blob/spring-boot-1/src/main/java/com/example/spring/actuatorsdemo/metrics/AlarmMetrics.java) class, and [Counters](https://micrometer.io/docs/concepts#_counters) declared in the [AlarmsController](https://github.com/IBM/spring-alarm-application/blob/spring-boot-1/src/main/java/com/example/spring/actuatorsdemo/rest/AlarmsController.java) class.

Spring metrics is one area that has seen significant change between Boot 1.x and Boot 2.x, with Boot 2.x choosing to use Micrometer as the API for Metrics. Micrometer describes itself as "LikeSLF4J but for Metrics", allowing the application code to write to a single interface, while it handles relaying the metrics to the configured monitoring system(s). 

For more information check the [Spring Documentation for Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-metrics.html)

> **Note** this application is using Micrometer for it's metrics, even in it's Boot 1.x version
> Micrometer is included as a transient dependency of the Prometheus support (see below!)

> **LAB:** Try adding another Counter, or Gauge, check that you can see it at http://127.0.0.1:9099/actuator/metrics (To see it's value visit http://127.0.0.1:9099/actuator/metrics/yournewmetricname)

## Prometheus endpoint 

Prometheus obtains it's metrics by querying an endpoint provided by the application. So to add Prometheus metrics to a Spring app, we need to have such an endpoint. In Boot 2.x, this is all handled by Micrometer, provided by Spring Actuator, but in Boot 1.x it was missing. Thankfully Micrometer was backported to Boot 1.x, enabling us to use the same approach in both frameworks!

To enable Prometheus support, we have to add the appropriate dependency...

- Boot 1.x (see [example](https://github.com/IBM/spring-alarm-application/blob/spring-boot-1/pom.xml#L55-L64))
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-spring-legacy</artifactId>
    <version>${micrometer.version}</version>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <version>${micrometer.version}</version>
</dependency>
```
- Boot 2.x (see [example](https://github.com/IBM/spring-alarm-application/blob/spring-boot-2/pom.xml#L42-L45))
```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

The Prometheus endpoint is not accessible by default, to expose it, you have to set 
some properties in `application.properties`

- Boot 1.x
  - `endpoints.prometheus.sensitive` must be set to `false` to allow access to the prometheus endpoint.
- Boot 2.x - `management.endpoints.web.exposure.include` must include `prometheus`
  - This property is a comma separated list of which endpoints should be exposed over http.
  - eg. _ `management.endpoints.web.exposure.include=health,prometheus`_

If required, the endpoint is also allowed through the web security configuration in `SecurityConfig.java`

- Boot 1.x - Not present. 
  - When `management.security.enabled` is false, actuator endpoints are accessible regardless
    of application security configuration.
- Boot 2.x
  - As part of the `WebSecurityConfigurerAdapter`s configuration of the http security, 
    - `.requestMatchers(EndpointRequest.to("prometheus").permitAll()`
    - _the `.to(...)` method can accept multiple args, eg `to("health","prometheus")`_

The endpoint is then reachable at

- Boot 1.x - `/prometheus`
- Boot 2.x - `/actuator/prometheus`

In a real deployment, Prometheus would then query this endpoint periodically to collect metrics. 
For the test purpose of this application, it's sufficient to view the endpoint in a browser, and 
note the metrics represent the expected values.

> **LAB:** Find one of the metrics added by the application (they start with `alarm`) and test that the values change when you perform the appropriate action via the [application ui](http://127.0.0.1:9099/web/)
