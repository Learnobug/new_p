package com.example.new_p.service;

import com.example.new_p.entity.RequestEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CacheKeyService {


    private RequestEntity requestEntity;
    public String generate_hash(HttpServletRequest req){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            String requesturi = req.getRequestURI();
            byte[] body = req.getInputStream().readAllBytes();
            String method = req.getMethod();
            Map<String,String> header = Collections.list(req.getHeaderNames()).stream()
                    .collect(Collectors.toMap(
                            h -> h,
                            h -> req.getHeader(h)
                    ));
            String data =
                    req.getMethod() +
                            req.getRequestURI() +
                            req.getQueryString() +
                            Arrays.toString(body);

            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
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
