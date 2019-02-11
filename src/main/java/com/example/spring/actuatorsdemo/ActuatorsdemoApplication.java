package com.example.spring.actuatorsdemo;

import com.example.spring.actuatorsdemo.metrics.AlarmMetrics;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class ActuatorsdemoApplication {

    @Autowired
    AlarmMetrics metrics;

    public static void main(String[] args) {
        SpringApplication.run(ActuatorsdemoApplication.class, args);
    }

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

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

}
