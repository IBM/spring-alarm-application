package org.ozzy.demo.metricsdemo.model.dao;

import java.sql.Time;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(includeFieldNames=true)
@Entity
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //because we have data in data.sql, and want to auto gen ids. 
    Long id;
    String name;
    Time start;
    Time end;    
    int priority;
}