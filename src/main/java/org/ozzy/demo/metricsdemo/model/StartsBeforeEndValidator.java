package org.ozzy.demo.metricsdemo.model;

import java.sql.Time;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.ozzy.demo.metricsdemo.model.dto.Alarm;

public class StartsBeforeEndValidator 
  implements ConstraintValidator<StartsBeforeEnd, Alarm> {

    public void initialize(StartsBeforeEnd constraintAnnotation) {
    }

    public boolean isValid(Alarm value, 
      ConstraintValidatorContext context) {

        Time start = value.getStart();
        Time end = value.getEnd();
        
        if (start !=null && end != null){
            return start.before(end);
        } else {
            return false;
        }
    }
}