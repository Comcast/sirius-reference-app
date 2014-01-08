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

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class KVRepositoryTest {

    private KVRepository underTest;

    @Before
    public void init() {
        underTest = new KVRepository();
    }

    @Test
    public void testPutGetRoundTrip() throws Exception {
        underTest.put("a", "b");

        assertEquals("b", underTest.get("a"));
    }

    @Test
    public void testDelete() throws Exception {
        underTest.put("a", "b");
        underTest.delete("a");

        assertNull(underTest.get("a"));
    }

    @Test
    public void testKeys() throws Exception {
        underTest.put("a", "b");
        underTest.put("b", "c");

        Set<String> keys = underTest.keys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("a"));
        assertTrue(keys.contains("b"));
    }
}
