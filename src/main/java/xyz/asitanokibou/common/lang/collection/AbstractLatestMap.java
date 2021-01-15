package xyz.asitanokibou.common.lang.collection;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class AbstractLatestMap<E, K> {

    protected final long retainSize;

    private final ConcurrentHashMap<Long, E> innerMap = new ConcurrentHashMap<>();
    private final Function<E, K> keyExtractor;
    private final Function<K,Long> keyHashing;

    public AbstractLatestMap(long size,
                             Function<E, K> keyExtractor,
                             Function<K,Long> keyHashing) {
        this.retainSize = size;
        this.keyExtractor = keyExtractor;
        this.keyHashing = keyHashing;
    }

    public void add(E e) {
        long index = keyHashing.apply(keyExtractor.apply(e));
        innerMap.put(index, e);
    }

    public E get(K key) {
        long index = keyHashing.apply(key);
        E t = innerMap.get(index);
        //因为旧的key会被新占用(但实际的key值可能不相等) 需要再判断E对象中的时间和指定的时间是否一致
        if (Optional.of(t)
                .map(keyExtractor)
                .filter(o -> o.equals(key))
                .isPresent()
        ) {
            return t;
        }
        return null;
    }

}
