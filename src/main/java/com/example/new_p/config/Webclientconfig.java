package com.example.new_p.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
public class Webclientconfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}
