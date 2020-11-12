package xyz.asitanokibou.common.lang.lambda;

import java.io.Serializable;

@FunctionalInterface
public interface SFunction<T,R> extends Serializable {

    @SuppressWarnings("UnusedReturnValue")
    R apply(T t);
}
