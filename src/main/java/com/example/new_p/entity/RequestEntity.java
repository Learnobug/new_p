package com.example.new_p.entity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.Collections;

@Getter
@Setter
public class RequestEntity {

    private String requestUri;
    private byte[] body;
    private HttpMethod method;
    private HttpHeaders headers;

    public RequestEntity(HttpServletRequest req) throws IOException {
        this.requestUri = req.getRequestURI();
        this.body = req.getInputStream().readAllBytes();
        this.method = HttpMethod.valueOf(req.getMethod());
        this.headers = extractHeaders(req);
    }

    private HttpHeaders extractHeaders(HttpServletRequest req) {
        HttpHeaders headers = new HttpHeaders();

        Collections.list(req.getHeaderNames()).forEach(h ->
                headers.addAll(h, Collections.list(req.getHeaders(h)))
        );

        return headers;
    }
}
