package com.ndrlslz.common;

import java.util.*;
import java.util.function.Consumer;

public class LinkedHashMultiMap<K, V> implements MultiMap<K, V> {
    private LinkedHashMap<K, Collection<V>> map;

    LinkedHashMultiMap() {
        this.map = new LinkedHashMap<>();
    }

    @Override
    public int size() {
        return map
                .values()
                .stream()
                .mapToInt(Collection::size).sum();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return map
                .values()
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(v -> v.equals(value));
    }

    @Override
    public Collection<V> getAll(K key) {
        return map.get(key);
    }

    public V get(K key) {
        Collection<V> values = getAll(key);
        if (values != null && !values.isEmpty()) {
            ArrayList<V> vs = new ArrayList<>(values);
            return vs.get(0);
        }
        return null;
    }

    public V get(K key, V defaultValue) {
        V value = get(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public Collection<V> set(K key, Collection<V> value) {
        if (map.containsKey(key)) {
            map.get(key).addAll(value);
        } else {
            map.put(key, value);
        }

        return getAll(key);
    }

    @Override
    public void set(K key, V value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            HashSet<V> set = new HashSet<>();
            set.add(value);
            map.put(key, set);
        }
    }

    @Override
    public Collection<V> remove(K key) {
        return map.remove(key);
    }

    @Override
    public void setAll(Map<? extends K, ? extends Collection<V>> m) {
        m.entrySet()
                .forEach((Consumer<Map.Entry<? extends K, ? extends Collection<V>>>) entry ->
                        set(entry.getKey(), entry.getValue()));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Collection<V>> values() {
        return map.values();
    }

    @Override
    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        return map.entrySet();
    }

    public void each(Function<K, V> function) {
        map.forEach((k, vs) -> vs.forEach(v -> {
            function.apply(k, v);
        }));
    }
}