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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    public Proxycontroller(String origin){
//        this.Origin = origin;
//    }
    @RequestMapping("/**")
    public void intercept(HttpServletRequest req) throws IOException, URISyntaxException {
        String key = this.cacheKeyService.generate_hash(req);
        // generate key

        CacheEntry entry = cacheManager.get(key);


        if (entry == null ){
             fromOriginAndCache(req);
             return;

        }

        fromCache(entry);
    }


    public CacheEntry fromCache(CacheEntry entry){
        Map<String,String> header = entry.getResponseHeader();
        header.put("X-CACHE","HIT");
        entry.setResponseHeader(header);
        return entry;
    }


    public void fromOriginAndCache(HttpServletRequest req) throws IOException, URISyntaxException {
        requestEntity =  new RequestEntity(req);
        URI originuri = new URI(this.origin);
        System.out.println(requestEntity.getMethod());
        System.out.println(requestEntity.getMethod());
        System.out.println(requestEntity.getHeaders());
        System.out.println(requestEntity.getBody());
        this.forwardservice.forward(requestEntity.getMethod(),originuri,requestEntity.getHeaders(), requestEntity.getBody());

    }
    // localhost:8080/api/lll
    // example.com
    // example.com/api/ll

    //check the req in cache
    // if cache- hit - present in cache - don't forward the req to server
    // if cache - miss- forward the request to server - and cache the response
    // 1) forwarding service
    // 2) caching storage implementation - LRU cache
    // 3) KEY CONSTRUCTION ->

}
