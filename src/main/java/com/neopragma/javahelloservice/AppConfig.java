package com.neopragma.javahelloservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.neopragma.javahellolib.Hello;

@Configuration
public class AppConfig {
    @Bean
    public Hello hello() {
        return new Hello();
    }
}