package org.ozzy.demo.metricsdemo.view;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import org.ozzy.demo.metricsdemo.model.dto.Alarm;
import org.ozzy.demo.metricsdemo.rest.AlarmsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ViewController {

    @Autowired
    AlarmsController alarms;

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
    public String addUser(@Valid Alarm alarm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-alarm";
        }
        alarms.addAlarm(alarm);
        return "redirect:/";
    }  
    
    @PostMapping("/update/{id}")
    public String updateAlarm(@PathVariable("id") long id, @Valid Alarm alarm, 
      BindingResult result, Model model) {
        if (result.hasErrors()) {
            alarm.setId(id);
            return "update-alarm";
        }     
        alarms.addAlarm(alarm);
        return "redirect:/";
    }  
    
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model) {
        Alarm alarm = alarms.getById(id);
        if(alarm==null){
            throw new IllegalArgumentException("Invalid alamrm id: "+id);
        }   
        alarms.deleteAlarm(alarm.getId());
        return "redirect:/";
    }

}