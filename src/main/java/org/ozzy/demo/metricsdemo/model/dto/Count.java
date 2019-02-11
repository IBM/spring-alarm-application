package org.ozzy.demo.metricsdemo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/** 
 * A Simple POJO to return Counts to callers of the REST api.
 * Note: this class is using lombok to create the setters/getters/constructors and toString methods.
 s*/

//lombok meta annotation to add setters/getters/constructors/toString
@Data
//customize lombok, ask for an all args constructor.
//(Needed for ModelMapper mapping)
@AllArgsConstructor
public class Count{
    Long count;
}