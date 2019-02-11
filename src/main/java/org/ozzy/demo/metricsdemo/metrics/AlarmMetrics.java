package org.ozzy.demo.metricsdemo.metrics;

import java.sql.Time;
import java.time.Instant;

import org.ozzy.demo.metricsdemo.persistence.Alarms;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * A simple component that adds 2 gauges that track db properties.
 */
@Component
public class AlarmMetrics {

    //we just need these to exist.
    //they are registered with the Registry on creation, and
    //will be found and queried whenever metrics are collected.

    @SuppressWarnings("unused")
    private final Gauge activeAlarms;
    @SuppressWarnings("unused")
    private final Gauge totalAlarms;

    private final Alarms a;

    public AlarmMetrics(MeterRegistry registry, Alarms alarms) {
        this.a = alarms;

        //create a gauge that when collected, will return the number of alarms active at that instant.
        activeAlarms = Gauge.builder("alarms.active", () -> a.countActive(new Time(Instant.now().toEpochMilli())))
                .description("Number of alarms currently active").register(registry);

        //create a gauge that tracks how many alarms are known to the db in total.
        totalAlarms = Gauge.builder("alarms.total", () -> a.countAll())
                .description("Number of alarms currently known").register(registry);
    }
}