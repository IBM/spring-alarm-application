package org.ozzy.demo.metricsdemo.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation that can be used to enforce 
 * start before end on Alarm DTOs.
 * 
 * This is the annotation part, the logic is in the 
 * 'validatedBy' class StartsBeforeEndValidator.class
 */
@Constraint(validatedBy = StartsBeforeEndValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface StartsBeforeEnd {
    String message() default "Start time must be before End time.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};    
}