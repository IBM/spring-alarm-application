package org.ozzy.demo.metricsdemo;

import org.modelmapper.ModelMapper;
import org.ozzy.demo.metricsdemo.metrics.AlarmMetrics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MetricsdemoApplication {

	@Autowired
	AlarmMetrics metrics;

	public static void main(String[] args) {
		SpringApplication.run(MetricsdemoApplication.class, args);
	}

	@Bean 
	public ModelMapper mapper(){
		return new ModelMapper();
	}

}

