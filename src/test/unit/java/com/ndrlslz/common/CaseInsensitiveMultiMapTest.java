package com.ndrlslz.common;

import org.junit.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CaseInsensitiveMultiMapTest {
    private CaseInsensitiveMultiMap<CharSequence> map = new CaseInsensitiveMultiMap<>();


    @Test
    public void containsKey() {
        map.put("Connection", "keep-alive");

        assertThat(map.containsKey("connection"), is(true));
    }

    @Test
    public void getAll() {
        map.put("connection", "keep-alive");
        map.put("connection", "close");

        assertThat(map.getAll("connection"), hasItems("keep-alive", "close"));
    }

    @Test
    public void get() {
        map.put("KEY", "Upper");
        map.put("key", "Lower");

        assertThat(map.get("key"), is("Lower"));
        assertThat(map.get("Key"), is("Upper"));
    }

    @Test
    public void putSingleValue() {
        map.put("Connection", "keep-alive");

        assertThat(map.get("CONNECtion"), is("keep-alive"));
    }

    @Test
    public void putValues() {
        map.put("Key1", setOf("value1", "value2"));
        map.put("Key2", setOf("value3"));

        assertThat(map.getAll("key1"), hasItems("value1", "value2"));
        assertThat(map.get("key2"), is("value3"));
    }

    @Test
    public void remove() {
        map.put("key1", "value1");

        map.remove("key1");

        assertThat(map.isEmpty(), is(true));
    }

    @Test
    public void putAll() {
        Map<CharSequence, Collection<CharSequence>> anotherMap = new HashMap<>();
        anotherMap.put("Key1", setOf("value1", "value2"));
        anotherMap.put("Key2", setOf("value3"));

        map.putAll(anotherMap);

        assertThat(map.getAll("key1"), hasItems("value1", "value2"));
        assertThat(map.get("key2"), is("value3"));
    }

    private Set<CharSequence> setOf(CharSequence... values) {
        return new HashSet<>(Arrays.asList(values));
    }
}