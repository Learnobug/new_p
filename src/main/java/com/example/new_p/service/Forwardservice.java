package com.example.new_p.service;

import com.example.new_p.config.Webclientconfig;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.time.Duration;

@Service
public class Forwardservice{


    private final WebClient webclient;

    public Forwardservice(WebClient webclient) {
        this.webclient = webclient;

    }

    public ResponseEntity<byte[]> forward(
            HttpMethod method,
            URI targetUri,
            HttpHeaders incomingHeaders,
            byte[] body
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

        return responseSpec.toEntity(byte[].class).block();
    }

}
