# Details

## Health check 

The health check endpoint is supplied via Spring Actuator. 

Actuators are enabled via this dependency in pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

The endpoint is made accessible in `application.properties`

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

If required, the endpoint is also allowed through the web security configuration in `SecurityConfig.java`

- Boot 1.x - Not present. 
  - When `management.security.enabled` is false, actuator endpoints are accessible regardless
    of application security configuration.
- Boot 2.x
  - As part of the `WebSecurityConfigurerAdapter`s configuration of the http security, 
    - `.requestMatchers(EndpointRequest.to("health").permitAll()`

The endpoint is then reachable at

- Boot 1.x - `/health`
- Boot 2.x - `/actuator/health`

Spring's health check endpoint is contributed to by `HealthIndicator`s. Spring derives the final value
for the health check by aggregating the HealthIndicators it finds, which are supplied by the various 
Spring dependencies you may have added to the application. In addition, you can create your own beans
that implement the `HealthIndicator` interface, and they will be taken into account also. 

For more information, check the [Spring Documentation for Health Information](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html#production-ready-health)

> **Note:** although it is very simple to author your own REST endpoint for health checks in Spring, doing so means you 
> lose this aggreation behavior, and may miss out on application health issues that Spring is aware of.
  
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

Spring metrics is one area that has seen significant change between Boot 1.x and Boot 2.x, with 
Boot 2.x choosing to use Micrometer as the API for Metrics. Micrometer describes itself as "Like
SLF4J but for Metrics", allowing the application code to write to a single interface, while it 
handles relaying the metrics to the configured monitoring system(s). 

For more information check the [Spring Documentation for Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-metrics.html)

> **Note** this application is using Micrometer for it's metrics, even in it's Boot 1.x version
> Micrometer is included as a transient dependency of the Prometheus support (see below!)

## Prometheus endpoint 

Prometheus obtains it's metrics by querying an endpoint provided by the application. 
So to add Prometheus metrics to a Spring app, we need to add such an endpoint. 
In Boot 2.x, this is all handled by Micrometer, provided by Spring Actuator, but in Boot 1.x 
it was missing. Thankfully Micrometer was backported to Boot 1.x, enabling us to use the 
same approach in both frameworks!

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

## Custom actuator

Although both Boot 1.x & Boot 2.x offer support for custom actuators, Boot 2.x's is significantly simpler. 

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

The Spring Actuator framework in Boot 2.x was given a significant overhaul, providing an infrastructure for hosting endpoints that's entirely independent of Spring MVC / WebFlux / Jersey etc. For more information, check the [Spring Blog Post on Actuator Endpoints in Boot 2.x](https://spring.io/blog/2017/08/22/introducing-actuator-endpoints-in-spring-boot-2-0)


## OpenLiberty Boost

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
