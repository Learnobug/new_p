package com.example.new_p.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class CacheEntry {
   // key-> []
    private final byte[] responsebody;
    private final HttpStatusCode statuscode;
    private final String serverURI;
    private HttpHeaders responseHeader;
    private Long expiretime;

    private   CacheEntry(byte[] responsebody, HttpStatusCode statuscode, String serverURI,Long time, HttpHeaders headers){
        this.responseHeader = new HttpHeaders();
        this.responseHeader.putAll(headers);
        this.responsebody = responsebody;
        this.statuscode = statuscode;
        this.serverURI = serverURI;
        this.expiretime = time;
    }


    public static CacheEntry fromResponse(
            ResponseEntity<byte[]> response,
            String serverURI,
            long ttlMillis
    ) {
        long expiresAt = Instant.now().toEpochMilli() + ttlMillis;

        return new CacheEntry(
                response.getBody(),
                response.getStatusCode(),
                serverURI,
                expiresAt,
                response.getHeaders()
        );
    }


    public boolean isExpired(){
        return Instant.now().toEpochMilli() > this.expiretime;
    }
}
