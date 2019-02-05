package org.ozzy.demo.metricsdemo.model.dto;

import java.sql.Time;

import org.ozzy.demo.metricsdemo.model.StartsBeforeEnd;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
@StartsBeforeEnd
public class Alarm {
    Long id;
    String name;
    Time start;
    Time end;
    int priority;
}