package com.example.spring.actuatorsdemo.model.dao;

import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

/**
 * A simple POJO that acts as a Data Access Object (DAO) for Spring Data.
 * Note: this class is using lombok to create the setters/getters/constructors and toString methods.
 */

//lombok meta annotation to add setters/getters/constructors/toString
@Data
//lombok, tailor the generated toString method to add field names.
@ToString(includeFieldNames=true)
//jpa/spring-data, this class is an Entity.
@Entity
public class Alarm {
    //id field is our Primary Key, and uses a Generated Value. 
    //lombok will author the getter/setters for this field.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //because we have data in data.sql, and want to auto gen ids. 
                                                        //otherwise, generated id's can overlap the preseeded data.
    Long id;

    //name/start/end/priority are just regaular fields. 
    //lombok will author the getters/setters for these too.
    String name;
    Time start;
    Time end;    
    int priority;
}