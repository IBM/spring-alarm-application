package org.ozzy.demo.metricsdemo;

import org.modelmapper.ModelMapper;
import org.ozzy.demo.metricsdemo.metrics.AlarmMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class MetricsdemoApplication {

    @Autowired
    AlarmMetrics metrics;

    public static void main(String[] args) {
        SpringApplication.run(MetricsdemoApplication.class, args);
    }

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

    // Simple custom liveness check
    @Endpoint(id = "liveness")
    @Component
    public class Liveness {
        @ReadOperation
        public String testLiveness() {
            return "{\"status\":\"UP\"}";
        }
    }

}
