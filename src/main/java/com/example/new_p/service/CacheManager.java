package com.example.new_p.service;

import com.example.new_p.entity.CacheEntry;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CacheManager {

    private final int MAX_CACHE_SIZE = 1000;

    private final Map<String, CacheEntry> cacheStore = Collections.synchronizedMap(
            new LinkedHashMap<String, CacheEntry>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, CacheEntry> eldest) {
                    // Evict if size exceeds the limit
                    return size() > MAX_CACHE_SIZE;
                }
            }
    );
    public void put(String key, CacheEntry entity){
        this.cacheStore.put(key,entity);
    }

    public CacheEntry get(String key){
        CacheEntry entry = cacheStore.get(key);
            if (entry == null) {
                return null;
            }

        if (entry.isExpired()) {
            cacheStore.remove(key);
            return null;
        }

        return entry;
    }

    public int getCurrentSize() {
        return cacheStore.size();
    }

    public void clearCache() {
        cacheStore.clear();
    }
}
