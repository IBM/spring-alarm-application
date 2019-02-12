# Spring Example Application

**Boot 1.5.x Version**

An example application written with Sprint Boot, showing simple usage of Spring Actuators, Metrics, Spring Data, ModelMapper and Lombok.

## Compiling

`mvn clean package`

## Running locally

`mvn spring-boot:run`

- Access the web UI at http://127.0.0.1:9099/web
- Health Check at http://127.0.0.1:9099/health
- Spring Metrics Overview at http://127.0.0.1:9099/metrics
- Prometheus Endpoint at http://127.0.0.1:9099/prometheus
- Custom Liveness Check at http://127.0.0.1:9099/liveness

## Building Docker image with OpenLiberty/Boost

`mvn boost:docker-build`

Details on built image

`docker image inspect actuatorsdemo:latest`

Run the image

`docker run -p 9099:9080 actuatorsdemo:latest`

(OpenLiberty/Boost runs by default on port 9080, we can use docker to put it at 9099 to match running locally)



