/*
 * Copyright 2013 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
