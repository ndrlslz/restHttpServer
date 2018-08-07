package com.ndrlslz.common;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CaseInsensitiveMultiMap<V> extends LinkedHashMultiMap<CharSequence, V> {
    private LinkedHashMultiMap<CharSequence, V> upperKeyMultiMap = new LinkedHashMultiMap<>();

    @Override
    public boolean containsKey(CharSequence key) {
        return upperKeyMultiMap.containsKey(key.toString().toLowerCase());
    }

    @Override
    public Collection<V> getAll(CharSequence key) {
        return Optional
                .ofNullable(CaseInsensitiveMultiMap.super.getAll(key))
                .orElseGet(() -> upperKeyMultiMap.getAll(key.toString().toLowerCase()));
    }

    @Override
    public V get(CharSequence key) {
        return Optional
                .ofNullable(CaseInsensitiveMultiMap.super.get(key))
                .orElseGet(() -> upperKeyMultiMap.get(key.toString().toLowerCase()));
    }

    @Override
    public Collection<V> set(CharSequence key, Collection<V> value) {
        upperKeyMultiMap.set(key.toString().toLowerCase(), value);
        return super.set(key, value);
    }

    @Override
    public void set(CharSequence key, V value) {
        upperKeyMultiMap.set(key.toString().toLowerCase(), value);
        super.set(key, value);
    }

    @Override
    public Collection<V> remove(CharSequence key) {
        upperKeyMultiMap.remove(key.toString().toLowerCase());
        return super.remove(key);
    }

    @Override
    public void setAll(Map<? extends CharSequence, ? extends Collection<V>> m) {
        m.entrySet()
                .forEach((Consumer<Map.Entry<? extends CharSequence, ? extends Collection<V>>>) entry ->
                        set(entry.getKey().toString().toLowerCase(), entry.getValue()));
        super.setAll(m);
    }
}
