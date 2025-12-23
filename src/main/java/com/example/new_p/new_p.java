package com.example.new_p;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class new_p implements ApplicationRunner {

    @Value("${origin.url}")
    private String origin;

    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(new_p.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        String port = environment.getProperty("local.server.port");
        System.out.println("Proxy server started at http://localhost:" + port);
    }
}
