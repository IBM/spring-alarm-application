package org.ozzy.demo.metricsdemo.persistence;

import java.sql.Time;
import java.util.List;

import org.ozzy.demo.metricsdemo.model.dao.Alarm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data Repository for Alarm Data Access Objects. 
 */
public interface Alarms extends Repository<Alarm,Long>{

    //Spring can generate the methods for these, based on the Naming of the methods.
    //Simple methods like 'findByX' right up to specifying ordering of returned values.
    
    public Alarm findById(Long id);
    public List<Alarm> findByStart(Time start);     
    public List<Alarm> findAllByOrderByStartAscPriorityAsc();    
    public Alarm save(Alarm a);
    public Alarm delete(Alarm a);    

    //when you need something a little more complicated than Spring Data can generate, 
    //you can use @Query to specify your own query to be executed, including arguments from the method.

    @Query("SELECT a FROM Alarm a WHERE a.start<=:time AND a.end>=:time ORDER BY a.priority ASC")
    public List<Alarm> findAlarmsActiveAtTime(@Param("time") Time t);
    @Query("SELECT COUNT(ID) FROM Alarm")
    public Long countAll();
    @Query("SELECT COUNT(ID) FROM Alarm a WHERE a.start<=:time AND a.end>=:time")
    public Long countActive(@Param("time") Time t);

}