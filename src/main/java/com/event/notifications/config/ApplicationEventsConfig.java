package com.event.notifications.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;

@Configuration
public class ApplicationEventsConfig {

    private static final Logger log = LoggerFactory.getLogger(ApplicationEventsConfig.class);

    @Bean(name = "applicationEventMulticaster")
    SimpleApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setErrorHandler(error -> log.error("Event listener execution failed", error));
        return multicaster;
    }
}
