package org.ozzy.demo.metricsdemo.metrics;

import java.sql.Time;
import java.time.Instant;

import org.ozzy.demo.metricsdemo.persistence.Alarms;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class AlarmMetrics {

    private final Gauge activeAlarms;
    private final Gauge totalAlarms;

    private final Alarms a;

    public AlarmMetrics(MeterRegistry registry, Alarms alarms) {
        this.a = alarms;

        activeAlarms = Gauge.builder("alarms.active", () -> a.countActive(new Time(Instant.now().toEpochMilli())))
                .description("Number of alarms currently active").register(registry);

        totalAlarms = Gauge.builder("alarms.total", () -> a.countAll())
                .description("Number of alarms currently known").register(registry);
    }
}