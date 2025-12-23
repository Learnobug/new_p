package com.example.new_p.service;

import com.example.new_p.config.Webclientconfig;
import com.example.new_p.entity.CacheEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.Duration;

import static com.example.new_p.entity.CacheEntry.fromResponse;


@Service
public class Forwardservice{


    private final WebClient webclient;

    @Autowired
    private CacheManager cacheManager;


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

        // 🔥 CALL ORIGIN ONLY ONCE
        ResponseEntity<byte[]> originResponse =
                responseSpec.toEntity(byte[].class).block();

        // cache it
        CacheEntry cacheEntry = CacheEntry.fromResponse(
                originResponse,
                targetUri.toString(),
                Duration.ofMinutes(5).toMillis()
        );
        cacheManager.put(key, cacheEntry);

        System.out.println("cacheEntry" + cacheEntry);
        // 🔥 add X-Cache: MISS
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(originResponse.getHeaders());
        headers.add("X-Cache", "MISS");

        System.out.println("headers" + headers);
        return new ResponseEntity<>(
                originResponse.getBody(),
                headers,
                originResponse.getStatusCode()
        );
    }

}
