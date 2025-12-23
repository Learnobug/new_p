package com.example.new_p.controller;

import com.example.new_p.entity.CacheEntry;
import com.example.new_p.entity.RequestEntity;
import com.example.new_p.service.CacheKeyService;
import com.example.new_p.service.CacheManager;
import com.example.new_p.service.DiskCacheManager;
import com.example.new_p.service.Forwardservice;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class Proxycontroller {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheKeyService cacheKeyService;

    @Autowired
    private Forwardservice forwardservice;

    @Autowired
    private DiskCacheManager diskCacheManager;

    @Value("${origin.url}")
    private String origin;

    @RequestMapping("/**")
    public ResponseEntity<byte[]> intercept(HttpServletRequest req) throws IOException, URISyntaxException {
        String key = this.cacheKeyService.generate_hash(req);

        CacheEntry entry = cacheManager.get(key);
        CacheEntry diskEntry = diskCacheManager.get(key);

        if (entry == null && diskEntry == null) {
            return fromOriginAndCache(req, key);
        }

        if (entry != null) {
            return fromCache(entry, key);
        }
        return fromDiskCache(diskEntry);
    }

    public ResponseEntity<byte[]> fromDiskCache(CacheEntry entry) throws IOException {
        System.out.println("Cache HIT from disk");
        HttpHeaders headers = new HttpHeaders();
        entry.getResponseHeader().forEach(headers::put);
        headers.add("X-CACHE", "HIT");

        return new ResponseEntity<byte[]>(
                entry.getResponsebody(),
                headers,
                HttpStatus.valueOf(entry.getStatuscode())
        );
    }

    public ResponseEntity<byte[]> fromCache(CacheEntry entry, String key) throws IOException {
        System.out.println("Cache HIT from memory");
        HttpHeaders headers = new HttpHeaders();
        entry.getResponseHeader().forEach(headers::put);
        headers.add("X-CACHE", "HIT");
        this.diskCacheManager.put(key, entry);
        return new ResponseEntity<byte[]>(
                entry.getResponsebody(),
                headers,
                HttpStatus.valueOf(entry.getStatuscode())
        );
    }

    public ResponseEntity<byte[]> fromOriginAndCache(HttpServletRequest req, String key) throws IOException, URISyntaxException {
        System.out.println("Cache MISS");
        RequestEntity requestEntity = new RequestEntity(req);
        URI targetUri = UriComponentsBuilder
                .fromUriString(origin)
                .path(req.getRequestURI())
                .query(req.getQueryString())
                .build(true)
                .toUri();

        return this.forwardservice.forward(requestEntity.getMethod(), targetUri, requestEntity.getHeaders(), requestEntity.getBody(), key);
    }

}
