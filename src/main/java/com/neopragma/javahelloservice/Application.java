package com.neopragma.javahelloservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
 
@ComponentScan
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
 
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
}