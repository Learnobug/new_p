package com.example.new_p.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CacheEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    private final byte[] responsebody;
    private final int statuscode;               // int instead of HttpStatus
    private final String serverURI;
    private Map<String, List<String>> responseHeader; // serializable
    private Long expiretime;

    public CacheEntry(byte[] responsebody, int statuscode, String serverURI,
                      long expiretime, Map<String, List<String>> headers) {
        this.responsebody = responsebody;
        this.statuscode = statuscode;
        this.serverURI = serverURI;
        this.expiretime = expiretime;
        this.responseHeader = new HashMap<>(headers);
    }

    public static CacheEntry fromResponse(
            byte[] body,
            int statusCode,
            String serverURI,
            HttpHeaders headers,
            long ttlMillis) {

        Map<String, List<String>> headerMap = new HashMap<>();
        headers.forEach((key, values) -> headerMap.put(key, List.copyOf(values)));
        long expiresAt = Instant.now().toEpochMilli() + ttlMillis;
        return new CacheEntry(body, statusCode, serverURI, expiresAt, headerMap);
    }

    public boolean isExpired() {
        return Instant.now().toEpochMilli() > this.expiretime;
    }
}
