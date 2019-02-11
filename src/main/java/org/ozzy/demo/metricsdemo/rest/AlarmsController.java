package org.ozzy.demo.metricsdemo.rest;

import java.sql.Time;
import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.ozzy.demo.metricsdemo.model.dto.Alarm;
import org.ozzy.demo.metricsdemo.model.dto.Count;
import org.ozzy.demo.metricsdemo.persistence.Alarms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Spring REST Controller supplying the Alarm application REST API.
 * 
 * This class uses two different sets of POJOs, 
 *  - the Data Access Objects, to talk to the persistence layer
 *  - the Data Transfer Objects, to talk to/from the REST consumer.
 * 
 * It uses ModelMapper to automatically convert from one type to another.
 * 
 */
@RestController
//make the entire API sit under "/alarms"
@RequestMapping("/alarms")
public class AlarmsController {

    //autowire in the Spring Data generated repository for our Alarm table.
    @Autowired
    Alarms alarms;

    //autowire the model mapper we'll use to flip between DAO/DTO objects
    @Autowired
    ModelMapper mapper;

    //a couple of counters for extra metrics.
    private final Counter deleteCounter;
    private final Counter saveCounter;

    //note the MeterRegistry will be passed to the constructor here by Spring
    //enabling us to create the counters we will update for our metrics.
    public AlarmsController(MeterRegistry mr){
        saveCounter = mr.counter("alarms.saved");
        deleteCounter = mr.counter("alarms.deleted");
    }

    //we disabled global timing of all REST/MVC endpoints in application.properties
    //but we wish to Time this method as an example of selectively timing methods 
    //for metrics.
    @Timed
    @GetMapping({"/",""})
    public List<Alarm> getAll(){    
        return mapper.map(alarms.findAllByOrderByStartAscPriorityAsc(),new TypeToken<List<Alarm>>() {}.getType());
    }

    @GetMapping({"/{id}"})
    public Alarm getById(@PathVariable Long id){    
        return mapper.map(alarms.findById(id),Alarm.class);
    }

    @GetMapping({"/start/{time}"})
    public List<Alarm> getForStartTime(@PathVariable Time time){
        return mapper.map(alarms.findByStart(time),new TypeToken<List<Alarm>>() {}.getType());
    }

    @GetMapping({"/active","/active/","/active/now"})
    public List<Alarm> getActiveNow(){
        return mapper.map(alarms.findAlarmsActiveAtTime(new Time(Instant.now().toEpochMilli())),new TypeToken<List<Alarm>>() {}.getType());
    }

    @GetMapping({"/active/{time}"})
    public List<Alarm> getActiveAtTime(@PathVariable Time time){
        return mapper.map(alarms.findAlarmsActiveAtTime(time),new TypeToken<List<Alarm>>() {}.getType());
    }

    @GetMapping({"/count","/count/","/count/all"})
    public Count getAlarmCount(){
        return new Count(alarms.countAll());
    }

    @GetMapping({"/count/{time}"})
    public Count getActiveCountAtTime(@PathVariable Time time){
        return new Count(alarms.countActive(time));
    }

    @PostMapping({"/",""})
    public Alarm addAlarm(@RequestBody Alarm toAdd) {
        //update the save counter (we should really only do this on a successful save!)
        saveCounter.increment();
        return mapper.map(alarms.save(mapper.map(toAdd, org.ozzy.demo.metricsdemo.model.dao.Alarm.class)),Alarm.class);
    }

    @DeleteMapping({"/{id}"})
    public Alarm deleteAlarm(@PathVariable Long id){
        Alarm alarm = mapper.map(alarms.findById(id),Alarm.class);
        if(alarm!=null){
            alarms.delete(alarms.findById(id));
            //update the delete counter.
            deleteCounter.increment();
        }
        return alarm;
    }

}