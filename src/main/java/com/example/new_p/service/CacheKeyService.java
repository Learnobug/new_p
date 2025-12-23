package com.example.new_p.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Component
public class CacheKeyService {

    public String generate_hash(HttpServletRequest req) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            byte[] body = req.getInputStream().readAllBytes();
            String data =
                    req.getMethod() +
                            req.getRequestURI() +
                            req.getQueryString() +
                            Arrays.toString(body);

            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
