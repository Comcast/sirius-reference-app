package com.comcast.xfinity.sirius.refapplication.store;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple thread-safe repository. Store and retrieve values.
 */
public class KVRepository {
    Map<String, String> backend = new ConcurrentHashMap<String, String>();

    public String get(String key) {
        return backend.get(key);
    }

    public void put(String key, String body) {
        backend.put(key, body);
    }

    public void delete(String key) {
        backend.remove(key);
    }

    public Set<String> keys() {
        return backend.keySet();
    }
}
