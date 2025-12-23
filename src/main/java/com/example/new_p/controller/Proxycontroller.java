package com.example.new_p.controller;
import com.example.new_p.entity.CacheEntry;
import com.example.new_p.entity.RequestEntity;
import com.example.new_p.service.CacheKeyService;
import com.example.new_p.service.CacheManager;
import com.example.new_p.service.Forwardservice;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.util.UriComponentsBuilder.*;

@RestController
public class Proxycontroller {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheKeyService cacheKeyService;

    @Autowired
    private Forwardservice forwardservice;


    private RequestEntity requestEntity;

    @Value("${origin.url}")
    private String origin;


    @RequestMapping("/**")
    public ResponseEntity<byte[]> intercept(HttpServletRequest req) throws IOException, URISyntaxException {
        String key = this.cacheKeyService.generate_hash(req);

        CacheEntry entry = cacheManager.get(key);


        if (entry == null ){
             return fromOriginAndCache(req);
        }

        return fromCache(entry);
    }


    public ResponseEntity<byte[]> fromCache(CacheEntry entry) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(entry.getResponseHeader());
        headers.add("X-Cache", "HIT");
        return new ResponseEntity<>(
                entry.getResponsebody(),
                headers,
                Integer.parseInt(entry.getStatuscode())
        );
    }


    public ResponseEntity<byte[]> fromOriginAndCache(HttpServletRequest req) throws IOException, URISyntaxException {
        requestEntity =  new RequestEntity(req);
        URI targetUri = UriComponentsBuilder
                .fromUriString(origin)     // ✅ ensures scheme + host
                .path(req.getRequestURI())
                .query(req.getQueryString())
                .build(true)
                .toUri();


        return this.forwardservice.forward(requestEntity.getMethod(),targetUri,requestEntity.getHeaders(), requestEntity.getBody());

    }

}
