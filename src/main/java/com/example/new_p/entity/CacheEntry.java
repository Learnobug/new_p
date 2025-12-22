package com.example.new_p.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class CacheEntry {
   // key-> []
    private final byte[] responsebody;
    private final String statuscode;
    private final String serverURI;
    private Map<String, String> responseHeader = new HashMap<>();
    private Long time;
    public  CacheEntry(byte[] responsebody, String statuscode, String serverURI,Long time, Map<String, String> headers){
        this.responseHeader = headers;
        this.responsebody = responsebody;
        this.statuscode = statuscode;
        this.serverURI = serverURI;
        this.time = time;
    }

    public boolean isExpired(){
        return Instant.now().toEpochMilli() > this.time;
    }
}
