package com.example.new_p.service;

import com.example.new_p.entity.CacheEntry;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DiskCacheManager {

    private static final String CACHE_DIR = "cache-store";

    public DiskCacheManager() throws IOException {
        Files.createDirectories(Paths.get(CACHE_DIR));
    }

    private Path file(String key) {
        return Paths.get(CACHE_DIR, key + ".bin");
    }

    public void put(String key, CacheEntry entry) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(file(key)))) {
            oos.writeObject(entry);
        } catch (Exception ignored) {
        }
    }

    public CacheEntry get(String key) {
        Path path = file(key);

        if (!Files.exists(path)) return null;

        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(path))) {

            CacheEntry entry = (CacheEntry) ois.readObject();

            if (entry.isExpired()) {
                Files.deleteIfExists(path);
                return null;
            }

            return entry;

        } catch (Exception e) {
            return null;
        }
    }
}
