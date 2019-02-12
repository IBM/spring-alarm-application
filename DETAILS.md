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
  
  
## Spring metrics

Spring Metrics are part of the Spring Actuator, and are enabled by this dependency in `pom.xml`

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

If required, the endpoint is also allowed through the web security configuration in `SecurityConfig.java`

- Boot 1.x - Not present. 
  - When `management.security.enabled` is false, actuator endpoints are accessible regardless
    of application security configuration.
- Boot 2.x
  - As part of the `WebSecurityConfigurerAdapter`s configuration of the http security, 
    - `.requestMatchers(EndpointRequest.to("metrics").permitAll()`
    - _the `.to(...)` method can accept multiple args, eg `to("health","metrics")`_

## Prometheus endpoint 

**TODO**

## Custom actuator

**TODO**

## Openliberty boost

**TODO**
