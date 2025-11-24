package com.dactaphanmem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Nếu sử dụng giải pháp này, bạn không cần import R2dbcAutoConfiguration nữa
// @SpringBootApplication(exclude = { R2dbcAutoConfiguration.class })

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}