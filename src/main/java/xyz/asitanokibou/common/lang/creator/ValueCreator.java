package xyz.asitanokibou.common.lang.creator;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 值创造器
 * @author aimysaber@gmail.com
 */
public interface ValueCreator<T> extends Iterable<T> {

    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
