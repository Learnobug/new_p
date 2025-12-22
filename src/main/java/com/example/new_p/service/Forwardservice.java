package com.example.new_p.service;

import com.example.new_p.config.Webclientconfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Service
public class Forwardservice{


    private final WebClient webclient;

    public Forwardservice(WebClient webclient) {
        this.webclient = webclient;

    }

    public ResponseEntity<byte[]> forward(  HttpMethod method,
                                            URI targetUri,
                                            HttpHeaders incomingHeaders,
                                            @RequestBody(required = false) byte[] body){
        WebClient.RequestBodySpec req = this.webclient
                .method(method)
                .uri(targetUri)
                .headers(h -> {
                    h.addAll(incomingHeaders);
                    h.remove(HttpHeaders.HOST);
                });

        WebClient.ResponseSpec responseSpec =
                (body != null && body.length > 0)
                        ? req.bodyValue(body).retrieve()
                        : req.retrieve();

        return responseSpec.toEntity(byte[].class).block();
    }
}
