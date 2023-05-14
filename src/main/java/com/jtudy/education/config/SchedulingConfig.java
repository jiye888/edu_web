package com.jtudy.education.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class SchedulingConfig {

    @Bean
    public AuthScheduling authScheduling() {
        return new AuthScheduling();
    }

}
