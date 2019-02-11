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


