package org.ozzy.demo.metricsdemo.config;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .headers()
            .frameOptions().sameOrigin().and()
        .csrf()
            .disable()
        .authorizeRequests()
            .requestMatchers(EndpointRequest.to("health","metrics","prometheus"))
                .permitAll()
            .antMatchers("/shoes/**")
                .permitAll()
            .antMatchers("/h2-console/**")
                .permitAll();
    }
}