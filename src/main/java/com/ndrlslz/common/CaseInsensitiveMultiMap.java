package com.ndrlslz.common;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CaseInsensitiveMultiMap<V> extends LinkedHashMultiMap<CharSequence, V> {
    private LinkedHashMultiMap<CharSequence, V> upperKeyMultiMap = new LinkedHashMultiMap<>();

    @Override
    public boolean containsKey(CharSequence key) {
        return upperKeyMultiMap.containsKey(key.toString().toUpperCase()) || super.containsKey(key);
    }

    @Override
    public Collection<V> getAll(CharSequence key) {
        return Optional
                .ofNullable(upperKeyMultiMap.getAll(key.toString().toUpperCase()))
                .orElseGet(() -> CaseInsensitiveMultiMap.super.getAll(key));
    }

    @Override
    public V get(CharSequence key) {
        return Optional
                .ofNullable(upperKeyMultiMap.get(key.toString().toUpperCase()))
                .orElseGet(() -> CaseInsensitiveMultiMap.super.get(key));
    }

    @Override
    public Collection<V> put(CharSequence key, Collection<V> value) {
        upperKeyMultiMap.put(key.toString().toUpperCase(), value);
        return super.put(key, value);
    }

    @Override
    public void put(CharSequence key, V value) {
        upperKeyMultiMap.put(key.toString().toUpperCase(), value);
        super.put(key, value);
    }

    @Override
    public Collection<V> remove(CharSequence key) {
        upperKeyMultiMap.remove(key.toString().toUpperCase());
        return super.remove(key);
    }

    @Override
    public void putAll(Map<? extends CharSequence, ? extends Collection<V>> m) {
        m.entrySet()
                .forEach((Consumer<Map.Entry<? extends CharSequence, ? extends Collection<V>>>) entry ->
                        put(entry.getKey().toString().toUpperCase(), entry.getValue()));
        super.putAll(m);
    }
}
