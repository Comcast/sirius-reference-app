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
