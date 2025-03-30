package org.example.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class LFUCache<T> {

    private static final Logger logger = Logger.getLogger(LFUCache.class.getName());

    private final int maxCapacity;
    private final Map<Long, CacheEntry<T>> cache = new HashMap<>();

    protected static class CacheEntry<T> {
        T value;
        int frequency;

        CacheEntry(T value) {
            this.value = value;
            this.frequency = 1;
        }
    }

    protected LFUCache(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        logger.log(Level.INFO, "LFUCache initialized with max capacity: " + maxCapacity);
    }

    public synchronized T get(Long id) {
        CacheEntry<T> entry = cache.get(id);
        if (entry != null) {
            entry.frequency++;
            logger.log(Level.INFO, "Cache hit for key: " + id + ", frequency: " + entry.frequency);
            return entry.value;
        }
        logger.log(Level.INFO, "Cache miss for key: " + id);
        return null;
    }

    public synchronized void put(Long id, T value) {
        if (cache.containsKey(id)) {
            CacheEntry<T> entry = cache.get(id);
            entry.value = value;
            entry.frequency++;
            logger.log(Level.INFO, "Cache update for key: " + id + ", frequency: " + entry.frequency);
        } else {
            if (cache.size() >= maxCapacity) {
                logger.log(Level.INFO, "Cache is full, evicting least frequently used entry");
                evictLeastFrequentlyUsed();
            }
            cache.put(id, new CacheEntry<>(value));
            logger.log(Level.INFO, "Cache put for key: " + id + ", frequency: 1");
        }
    }

    private void evictLeastFrequentlyUsed() {
        Long lfuKey = null;
        int minFrequency = Integer.MAX_VALUE;

        for (Map.Entry<Long, CacheEntry<T>> entry : cache.entrySet()) {
            if (entry.getValue().frequency < minFrequency) {
                minFrequency = entry.getValue().frequency;
                lfuKey = entry.getKey();
            }
        }
        if (lfuKey != null) {
            cache.remove(lfuKey);
            logger.log(Level.INFO, "Evicted key: " + lfuKey + " with frequency: " + minFrequency);
        }
    }

    public synchronized void remove(Long id) {
        cache.remove(id);
        logger.log(Level.INFO, "Removed key: " + id);
    }

    public synchronized void clear() {
        cache.clear();
        logger.log(Level.INFO, "Cache cleared");
    }
}