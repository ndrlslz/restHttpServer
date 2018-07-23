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
        map.put("Connection", "keep-alive");
        map.put("connection", "close");

        assertThat(map.getAll("connection"), hasItems("keep-alive", "close"));
    }

    @Test
    public void get() {
        map.put("Connection", "keep-alive");

        assertThat(map.get("connection"), is("keep-alive"));
    }

    @Test
    public void putSingleValue() {
        map.put("Connection", "keep-alive");


        assertThat(map.get("CONNECtion"), is("keep-alive"));
    }

    @Test
    public void putValues() {
        map.put("Key1", listOf("value1", "value2"));
        map.put("Key2", listOf("value3"));

        assertThat(map.get("key1"), is("value1"));
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
        anotherMap.put("Key1", listOf("value1", "value2"));
        anotherMap.put("Key2", listOf("value3"));

        map.putAll(anotherMap);

        assertThat(map.get("key1"), is("value1"));
        assertThat(map.get("key2"), is("value3"));
    }

    private ArrayList<CharSequence> listOf(CharSequence... values) {
        return new ArrayList<>(Arrays.asList(values));
    }
}