package com.ndrlslz.common;

import java.util.*;
import java.util.function.Consumer;

public class MultiMap<K, V> implements Map<K, Collection<V>> {
    private HashMap<K, Collection<V>> map;

    MultiMap() {
        this.map = new HashMap<>();
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
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map
                .values()
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(v -> v.equals(value));
    }

    @Override
    public Collection<V> get(Object key) {
        return map.get(key);
    }

    @Override
    public Collection<V> put(K key, Collection<V> value) {
        if (this.containsKey(key)) {
            map.get(key).addAll(value);
        } else {
            map.put(key, value);
        }

        return this.get(key);
    }

    public void putSingleValue(K key, V value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            ArrayList<V> list = new ArrayList<>();
            list.add(value);
            map.put(key, list);
        }
    }

    @Override
    public Collection<V> remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends Collection<V>> m) {
        m.entrySet()
                .forEach((Consumer<Entry<? extends K, ? extends Collection<V>>>) entry -> this.put(entry.getKey(), entry.getValue()));
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
    public Set<Entry<K, Collection<V>>> entrySet() {
        return map.entrySet();
    }

    public void each(Function<K, V> function) {
        map.forEach((k, vs) -> vs.forEach(v -> {
            function.apply(k, v);
        }));
    }
}