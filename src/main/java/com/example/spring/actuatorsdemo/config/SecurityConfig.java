package com.example.spring.actuatorsdemo.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * This class supplies the security configuration for the Web/Rest endpoints of this Application.
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .headers()
            //enable SAMEORIGIN header (required for h2-console)
            .frameOptions().sameOrigin().and() 
        .csrf()
            //disable spring csrf protection.
            .disable()        
        .authorizeRequests()
            //allow access to the specified actuator endpoints
            .requestMatchers(EndpointRequest.to("health","metrics","prometheus","liveness"))
                .permitAll()
            //allow access to the rest api
            .antMatchers("/alarms/**")
                .permitAll()
            //allow access to the web ui
            .antMatchers("/web/**")
                .permitAll()            
            //allow access to the h2-console    
            .antMatchers("/h2-console/**")
                .permitAll()
        .anyRequest()
            //deny everything else.
            .denyAll();
    }
}