package com.ndrlslz.common;

@FunctionalInterface
public interface Function<K, V> {
    void apply(K key, V value);
}
