package com.ndrlslz.common;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MultiMapTest {
    private MultiMap<String, String> multiMap;

    @Before
    public void setUp() {
        multiMap = new MultiMap<>();
    }

    @Test
    public void shouldGetSizeOfMultiMap() {
        multiMap.put("key1", asList("123", "456"));
        multiMap.put("key2", singletonList("678"));
        multiMap.put("key3", asList("a", "b", "c"));

        assertThat(multiMap.size(), is(6));
    }

    @Test
    public void shouldNotBeEmpty() {
        multiMap.put("key1", singletonList("value1"));

        assertThat(multiMap.isEmpty(), is(false));
    }

    @Test
    public void shouldContainsKey() {
        multiMap.put("key1", singletonList("value1"));

        assertThat(multiMap.containsKey("key1"), is(true));
    }

    @Test
    public void shouldContainsValues() {
        multiMap.put("key1", asList("123", "456"));
        multiMap.put("key2", singletonList("678"));
        multiMap.put("key3", asList("a", "b", "c"));

        assertThat(multiMap.containsValue("c"), is(true));
    }

    @Test
    public void shouldGetValue() {
        multiMap.put("key1", asList("123", "456"));
        multiMap.put("key2", singletonList("value2"));

        assertThat(multiMap.get("key1"), is(asList("123", "456")));
        assertThat(multiMap.get("key2"), is(singletonList("value2")));
    }

    @Test
    public void shouldPutEntry() {
        multiMap.put("key1", asList("value1", "value2"));

        assertThat(multiMap.get("key1"), is(asList("value1", "value2")));
    }

    @Test
    public void shouldRemoveKey() {
        multiMap.put("key1", asList("value1", "value2"));

        multiMap.remove("key1");

        assertThat(multiMap.isEmpty(), is(true));
    }

    @Test
    public void shouldPutAll() {
        MultiMap<String, String> map = new MultiMap<>();
        map.put("key1", singletonList("value1"));
        map.put("key2", asList("value2", "value3"));
        multiMap.put("key4", singletonList("value4"));

        multiMap.putAll(map);

        assertThat(multiMap.size(), is(4));
        assertThat(multiMap.get("key1"), is(singletonList("value1")));
        assertThat(multiMap.get("key2"), is(asList("value2", "value3")));
    }

    @Test
    public void shouldClear() {
        multiMap.put("key1", asList("123", "456"));
        multiMap.put("key2", singletonList("value2"));

        multiMap.clear();

        assertThat(multiMap.isEmpty(), is(true));
    }

    @Test
    public void shouldGetKeySet() {
        multiMap.put("key1", asList("123", "456"));
        multiMap.put("key2", singletonList("value2"));

        Set<String> keySet = multiMap.keySet();

        assertThat(keySet.containsAll(asList("key1", "key2")), is(true));
    }

    @Test
    public void shouldGetValues() {
        multiMap.put("key1", asList("123", "456"));
        multiMap.put("key2", singletonList("value2"));

        Collection<Collection<String>> values = multiMap.values();

        assertThat(values.contains(singletonList("value2")), is(true));
        assertThat(values.contains(asList("123", "456")), is(true));
    }

    @Test
    public void shouldGetEntrySet() {
        multiMap.put("key1", asList("123", "456"));
        multiMap.put("key2", singletonList("value2"));

        Set<Map.Entry<String, Collection<String>>> entries = multiMap.entrySet();

        assertThat(entries.size(), is(2));
    }

    @Test
    public void shouldIterateMultiMap() {
        AtomicInteger sum = new AtomicInteger(0);
        multiMap.put("key1", asList("123", "456"));
        multiMap.put("key2", singletonList("value2"));

        multiMap.each((key, value) -> sum.incrementAndGet());

        assertThat(sum.get(), is(3));
    }
}