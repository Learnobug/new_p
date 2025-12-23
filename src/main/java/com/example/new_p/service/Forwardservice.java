package com.example.new_p.service;

import com.example.new_p.entity.CacheEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.Duration;

@Service
public class Forwardservice {

    private final WebClient webclient;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DiskCacheManager diskCacheManager;

    @Autowired
    private CacheKeyService cacheKeyService;

    public Forwardservice(WebClient webclient) {
        this.webclient = webclient;
    }

    public ResponseEntity<byte[]> forward(
            HttpMethod method,
            URI targetUri,
            HttpHeaders incomingHeaders,
            byte[] body,
            String key
    ) {
        WebClient.RequestBodySpec req = webclient
                .method(method)
                .uri(targetUri)
                .headers(h -> {
                    h.addAll(incomingHeaders);
                    h.remove(HttpHeaders.HOST);
                    h.remove(HttpHeaders.CONNECTION);
                    h.remove(HttpHeaders.TRANSFER_ENCODING);
                    h.remove(HttpHeaders.CONTENT_LENGTH);
                });

        WebClient.ResponseSpec responseSpec =
                (body != null && body.length > 0 && method != HttpMethod.GET)
                        ? req.bodyValue(body).retrieve()
                        : req.retrieve();

        ResponseEntity<byte[]> originResponse =
                responseSpec.toEntity(byte[].class).block();

        CacheEntry cacheEntry = CacheEntry.fromResponse(
                originResponse.getBody(),
                originResponse.getStatusCode().value(),
                targetUri.toString(),
                originResponse.getHeaders(),
                Duration.ofMinutes(5).toMillis()
        );
        cacheManager.put(key, cacheEntry);
        this.diskCacheManager.put(key, cacheEntry);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(originResponse.getHeaders());
        headers.add("X-Cache", "MISS");

        return new ResponseEntity<>(
                originResponse.getBody(),
                headers,
                originResponse.getStatusCode()
        );
    }

}
