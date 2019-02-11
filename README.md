# Spring Example Application

An example application written with Sprint Boot, showing simple usage of Spring Actuators, Metrics, Spring Data, ModelMapper and Lombok.

## Compiling

`mvn clean package`

## Running locally

`mvn spring-boot:run`

Access the web UI at http://127.0.0.1:9099/web
Health Check at http://127.0.0.1:9099/actuator/health
Spring Metrics Overview at http://127.0.0.1:9099/actuator/metrics
Prometheus Endpoint at http://127.0.0.1:9099/actuator/prometheus
Custom Liveness Check at http://127.0.0.1:9099/actuator/liveness


