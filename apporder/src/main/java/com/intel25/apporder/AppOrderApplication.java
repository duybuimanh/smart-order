package com.intel25.apporder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AppOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppOrderApplication.class, args);
    }
}

