package com.decade.practice.utils;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.SetOperations;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DummyRedisSetOps<K, V> implements SetOperations<K, V> {

      @Override
      public Long add(K key, V... values) {
            return 0L;
      }

      @Override
      public Long remove(K key, Object... values) {
            return 0L;
      }

      @Override
      public V pop(K key) {
            return null;
      }

      @Override
      public List<V> pop(K key, long count) {
            return List.of();
      }

      @Override
      public Boolean move(K key, V value, K destKey) {
            return null;
      }

      @Override
      public Long size(K key) {
            return 0L;
      }

      @Override
      public Boolean isMember(K key, Object o) {
            return null;
      }

      @Override
      public Map<Object, Boolean> isMember(K key, Object... objects) {
            return Map.of();
      }

      @Override
      public Set<V> intersect(K key, K otherKey) {
            return Set.of();
      }

      @Override
      public Set<V> intersect(K key, Collection<K> otherKeys) {
            return Set.of();
      }

      @Override
      public Set<V> intersect(Collection<K> keys) {
            return Set.of();
      }

      @Override
      public Long intersectAndStore(K key, K otherKey, K destKey) {
            return 0L;
      }

      @Override
      public Long intersectAndStore(K key, Collection<K> otherKeys, K destKey) {
            return 0L;
      }

      @Override
      public Long intersectAndStore(Collection<K> keys, K destKey) {
            return 0L;
      }

      @Override
      public Set<V> union(K key, K otherKey) {
            return Set.of();
      }

      @Override
      public Set<V> union(K key, Collection<K> otherKeys) {
            return Set.of();
      }

      @Override
      public Set<V> union(Collection<K> keys) {
            return Set.of();
      }

      @Override
      public Long unionAndStore(K key, K otherKey, K destKey) {
            return 0L;
      }

      @Override
      public Long unionAndStore(K key, Collection<K> otherKeys, K destKey) {
            return 0L;
      }

      @Override
      public Long unionAndStore(Collection<K> keys, K destKey) {
            return 0L;
      }

      @Override
      public Set<V> difference(K key, K otherKey) {
            return Set.of();
      }

      @Override
      public Set<V> difference(K key, Collection<K> otherKeys) {
            return Set.of();
      }

      @Override
      public Set<V> difference(Collection<K> keys) {
            return Set.of();
      }

      @Override
      public Long differenceAndStore(K key, K otherKey, K destKey) {
            return 0L;
      }

      @Override
      public Long differenceAndStore(K key, Collection<K> otherKeys, K destKey) {
            return 0L;
      }

      @Override
      public Long differenceAndStore(Collection<K> keys, K destKey) {
            return 0L;
      }

      @Override
      public Set<V> members(K key) {
            return Set.of();
      }

      @Override
      public V randomMember(K key) {
            return null;
      }

      @Override
      public Set<V> distinctRandomMembers(K key, long count) {
            return Set.of();
      }

      @Override
      public List<V> randomMembers(K key, long count) {
            return List.of();
      }

      @Override
      public Cursor<V> scan(K key, ScanOptions options) {
            return null;
      }

      @Override
      public RedisOperations<K, V> getOperations() {
            return null;
      }
}
