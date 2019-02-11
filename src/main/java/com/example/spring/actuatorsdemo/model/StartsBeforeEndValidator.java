package com.example.spring.actuatorsdemo.model;

import java.sql.Time;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.spring.actuatorsdemo.model.dto.Alarm;

/**
 * Logic for @StartsBeforeEnd annotation. 
 * Tests an Alarm to ensure the Start time is before the End time.
 */
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
            //if either start or end is absent, we fail validation.
            //cannot test that which we do not have!
            return false;
        }
    }
}