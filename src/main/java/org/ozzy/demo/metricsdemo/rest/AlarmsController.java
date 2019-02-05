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

@RestController
@RequestMapping("/alarms")
public class AlarmsController {

    @Autowired
    Alarms alarms;

    @Autowired
    ModelMapper mapper;

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
        return mapper.map(alarms.save(mapper.map(toAdd, org.ozzy.demo.metricsdemo.model.dao.Alarm.class)),Alarm.class);
    }

    @DeleteMapping({"/{id}"})
    public Alarm deleteAlarm(@PathVariable Long id){
        Alarm alarm = mapper.map(alarms.findById(id),Alarm.class);
        if(alarm!=null){
            alarms.delete(alarms.findById(id));
            return alarm;
        }
        return null;
    }

}