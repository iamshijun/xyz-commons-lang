/*
package xyz.asitanokibou.common.lang.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import static java.util.concurrent.locks.ReentrantReadWriteLock.*;

//部分参考 commons-collections的 PassiveExpiringMap
public class ActiveExpiringMap<K, V> implements ConcurrentMap<K, V> {

    private final ConcurrentMap<K, V> delegate;
    private ConcurrentHashMap<K,Long> expireTimeMap;

    private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private ReadLock readLock = reentrantReadWriteLock.readLock();
//    private WriteLock writeLock = reentrantReadWriteLock.writeLock();

    public ActiveExpiringMap(ConcurrentMap<K, V> delegate) {
        this.delegate = delegate;
        this.expireTimeMap = new ConcurrentHashMap<>();


    }

    @Override
    public int size() {
        //较大开销
        return 0;
    }

    @Override
    public boolean isEmpty() {
        //较大开销
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        V v = delegate.get(key);
        if (v != null) {
            Long aLong = expireTimeMap.get(v);
            if (aLong != null) {
                //TODO 判断是否过期
            }else{
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        readLock.lock();
        try{

        }finally {
            readLock.unlock();
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V putIfAbsent(@Nullable K key, V value) {
        return null;
    }

    @Override
    public boolean remove(@Nullable Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(@Nullable K key, @Nullable V oldValue,@Nullable V newValue) {
        return false;
    }

    @Override
    public V replace(@Nullable K key,@Nullable V value) {
        return null;
    }
}
*/
