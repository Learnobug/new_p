package com.example.new_p;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class new_p implements ApplicationRunner {

    @Value("${origin.url}")
    private String origin;

    public static void main(String[] args) {
        SpringApplication.run(new_p.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("Proxy started with origin: " + origin);
    }
}
