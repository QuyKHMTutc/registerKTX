package com.dactaphanmem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.scheduling.annotation.EnableScheduling; // Removed

@SpringBootApplication
// @EnableScheduling // Removed
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
