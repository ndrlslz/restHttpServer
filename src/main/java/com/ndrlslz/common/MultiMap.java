package com.ndrlslz.common;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface MultiMap<K, V> {
    int size();

    boolean isEmpty();

    boolean containsKey(K key);

    boolean containsValue(V value);

    Collection<V> getAll(K key);

    V get(K key);

    Collection<V> set(K key, Collection<V> value);

    void set(K key, V value);

    Collection<V> remove(K key);

    void setAll(Map<? extends K, ? extends Collection<V>> m);

    void clear();

    Set<K> keySet();

    Collection<Collection<V>> values();

    Set<Map.Entry<K, Collection<V>>> entrySet();

    void each(Function<K, V> function);

}
