package org.ozzy.demo.metricsdemo.model.dto;

import java.sql.Time;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.ozzy.demo.metricsdemo.model.StartsBeforeEnd;

import lombok.Data;
import lombok.ToString;

/**
 * A simple POJO that acts as a Data Transfer Object (DTO) for Spring MVC/REST
 * Note: this class is using lombok to create the setters/getters/constructors and toString methods.
 */

//lombok meta annotation to add setters/getters/constructors/toString
@Data
//lombok, tailor the generated toString method to add field names.
@ToString(includeFieldNames=true)
//enable our custom validator for this bean, to ensure we only accept
//Alarms that end after they start (no spanning midnight!)
@StartsBeforeEnd
public class Alarm {
    //lombok will author the getter/setters for all fields.
    Long id;
    String name;
    Time start;
    Time end;
    //restrict the range of priority to between 0 and 10.
    @Min(value = 0, message="Min Priority is 0")
    @Max(value = 10, message="Max Priority is 10")
    int priority;
}