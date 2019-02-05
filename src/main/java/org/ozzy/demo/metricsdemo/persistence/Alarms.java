package org.ozzy.demo.metricsdemo.persistence;

import java.sql.Time;
import java.util.List;

import org.ozzy.demo.metricsdemo.model.dao.Alarm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface Alarms extends Repository<Alarm,Long>{
    public List<Alarm> findAllByOrderByStartAscPriorityAsc();

    public List<Alarm> findByStart(Time start); 

    public Alarm findById(Long id);

    @Query("SELECT a FROM Alarm a WHERE a.start<=:time AND a.end>=:time ORDER BY a.priority ASC")
    public List<Alarm> findAlarmsActiveAtTime(@Param("time") Time t);

    @Query("SELECT COUNT(ID) FROM Alarm")
    public Long countAll();

    @Query("SELECT COUNT(ID) FROM Alarm a WHERE a.start<=:time AND a.end>=:time")
    public Long countActive(@Param("time") Time t);

    public Alarm save(Alarm a);
    public Alarm delete(Alarm a);
}