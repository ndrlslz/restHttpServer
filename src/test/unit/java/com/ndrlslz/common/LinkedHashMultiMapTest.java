package com.ndrlslz.common;

import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LinkedHashMultiMapTest {
    private LinkedHashMultiMap<CharSequence, CharSequence> map;

    @Before
    public void setUp() {
        map = new LinkedHashMultiMap<>();
    }

    @Test
    public void shouldGetSizeOfMultiMap() {
        map.set("key1", setOf("123", "456"));
        map.set("key2", setOf("789"));
        map.set("key3", setOf("a", "b", "c"));

        assertThat(map.size(), is(6));
    }

    @Test
    public void shouldNotBeEmpty() {
        map.set("key1", setOf("value1"));

        assertThat(map.isEmpty(), is(false));
    }

    @Test
    public void shouldContainsKey() {
        map.set("key1", setOf("value1"));

        assertThat(map.containsKey("key1"), is(true));
    }

    @Test
    public void shouldContainsValues() {
        map.set("key1", setOf("123", "456"));
        map.set("key2", setOf("678"));
        map.set("key3", setOf("a", "b", "c"));

        assertThat(map.containsValue("c"), is(true));
    }

    @Test
    public void shouldGetValueByKey() {
        map.set("key1", setOf("123", "456"));
        map.set("key2", "value2");

        assertThat(map.get("key1"), is("123"));
        assertThat(map.get("key2"), is("value2"));
    }

    @Test
    public void shouldGetDefaultValueByKey() {
        map.set("key1", setOf("123", "456"));

        assertThat(map.get("key3", "default"), is("default"));
    }

    @Test
    public void shouldGetAllByKey() {
        map.set("key1", setOf("123", "456"));
        map.set("key2", "value2");

        assertThat(map.getAll("key1"), hasItems("123", "456"));
        assertThat(map.getAll("key2"), hasItems("value2"));
    }

    @Test
    public void shouldGetValues() {
        map.set("key1", setOf("123", "456"));
        map.set("key2", setOf("value2"));

        Collection<Collection<CharSequence>> values = map.values();

        assertThat(values, hasItems(setOf("value2")));
        assertThat(values, hasItems(setOf("123", "456")));
    }

    @Test
    public void shouldSetValues() {
        map.set("key1", setOf("value1", "value2"));
        map.set("key1", setOf("value3"));
        map.set("key2", setOf("value4", "value5"));

        assertThat(map.getAll("key1"), hasItems("value1", "value2", "value3"));
        assertThat(map.getAll("key2"), hasItems("value4", "value5"));
    }

    @Test
    public void shouldSetValue() {
        map.set("key1", setOf("value1"));
        map.set("key1", "value1");
        map.set("key1", "value2");
        map.set("key3", "value4");

        assertThat(map.size(), is(3));
        assertThat(map.getAll("key1"), hasItems("value1", "value2"));
    }

    @Test
    public void shouldRemoveKey() {
        map.set("key1", setOf("value1", "value2"));

        map.remove("key1");

        assertThat(map.isEmpty(), is(true));
    }

    @Test
    public void shouldSetAll() {
        HashMap<CharSequence, Collection<CharSequence>> map = new HashMap<>();
        map.put("key1", setOf("value1"));
        map.put("key2", setOf("value2", "value3"));
        this.map.set("key2", setOf("value4"));

        this.map.setAll(map);

        assertThat(this.map.size(), is(4));
        assertThat(this.map.getAll("key1"), is(setOf("value1")));
        assertThat(this.map.getAll("key2"), hasItems("value2", "value3", "value4"));
    }

    @Test
    public void shouldClear() {
        map.set("key1", setOf("123", "456"));
        map.set("key2", setOf("value2"));

        map.clear();

        assertThat(map.isEmpty(), is(true));
    }

    @Test
    public void shouldGetKeySet() {
        map.set("key1", setOf("123", "456"));
        map.set("key2", setOf("value2"));

        Set<CharSequence> keySet = map.keySet();

        assertThat(keySet.containsAll(asList("key1", "key2")), is(true));
    }

    @Test
    public void shouldGetEntrySet() {
        map.set("key1", setOf("123", "456"));
        map.set("key2", setOf("value2"));

        Set<Map.Entry<CharSequence, Collection<CharSequence>>> entries = map.entrySet();

        assertThat(entries.size(), is(2));
    }

    @Test
    public void shouldIterateMultiMap() {
        AtomicInteger sum = new AtomicInteger(0);
        map.set("key1", setOf("123", "456"));
        map.set("key2", setOf("value2"));

        map.each((key, value) -> sum.incrementAndGet());

        assertThat(sum.get(), is(3));
    }

    private Set<CharSequence> setOf(CharSequence... values) {
        return new HashSet<>(asList(values));
    }
}