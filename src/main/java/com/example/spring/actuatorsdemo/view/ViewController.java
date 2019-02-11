package com.example.spring.actuatorsdemo.view;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.spring.actuatorsdemo.model.dto.Alarm;
import com.example.spring.actuatorsdemo.rest.AlarmsController;

import io.micrometer.core.annotation.Timed;

/**
 * A (very) simple UI thrown together using Spring MVC.
 * Note that all all actual business logic is deferred to the REST api methods.
 * Eventually this UI will be replaced with something shiny, modern, and Javascripty.
 * Until then, this will have to do =)
 */
@Controller
@RequestMapping("/web")
public class ViewController {

    @Autowired
    AlarmsController alarms;

    //Add these attributes to the model for every page.
    @ModelAttribute
    public void populateModel(Model model){
        model.addAttribute("time", DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()));
        model.addAttribute("alarms", alarms.getAll());
        model.addAttribute("active", alarms.getActiveNow());
    }

    //index page
    @GetMapping({"/",""})
    public String index(Model model) {
        return "index";        
    }

    //addalarm page (invokes POST /addalarm)
    @GetMapping("/addalarm")
    public String showSignUpForm(Alarm alarm) {
        return "add-alarm";
    }

    //edit page, invokes POST /update/{id}
    @Timed
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Alarm alarm = alarms.getById(id);
        if(alarm==null){
            throw new IllegalArgumentException("Invalid alamrm id: "+id);
        } 
        model.addAttribute("alarm", alarm);
        return "update-alarm";
    }

    //callbacks from pages.

    @PostMapping("/addalarm")
    public String addAlarm(@Valid Alarm alarm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-alarm";
        }
        alarms.addAlarm(alarm);
        return "redirect:/web";
    }  
    
    @PostMapping("/update/{id}")
    public String updateAlarm(@PathVariable("id") long id, @Valid Alarm alarm, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            alarm.setId(id);
            return "update-alarm";
        }     
        alarms.addAlarm(alarm);
        return "redirect:/web";
    }  
    
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        Alarm alarm = alarms.getById(id);
        if(alarm==null){
            throw new IllegalArgumentException("Invalid alamrm id: "+id);
        }   
        alarms.deleteAlarm(alarm.getId());
        return "redirect:/web";
    }

}