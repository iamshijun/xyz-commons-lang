package xyz.asitanokibou.common.lang.creator;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * @author aimysaber@gmail.com
 */
@SuppressWarnings("rawtypes")
//因为像LocalDate,LocalDateTime 这两种类型 并不是直接implement Comparable<自身>的这里暂时只能用 raw type 的comparable
//public abstract class AbstractDataPointCreator<T extends Comparable<T>> implements ValueCreator<T> {
public abstract class AbstractDataPointCreator<T extends Comparable> implements ValueCreator<T> {

    /**
     * 起点
     */
    protected final T start;
    /**
     * 结束点
     */
    protected final T end;

    /**
     * 是否反向生成数据
     */
    protected final boolean reverse;
    /**
     * 是否循环产生数据 - e.g 到达最后时间点end后下一个时间点重新从start开始 持续生成时间点 hasNext永远为true
     */

    protected final boolean cycle;

    /**
     * 步进(/退)器
     */
    protected final Step<T> step;

//    private boolean threadSafe;

    public AbstractDataPointCreator(T start, T end, Step<T> step) {
        this(start, end, step, false);
    }

    public AbstractDataPointCreator(T start, T end, Step<T> step, boolean reverse) {
        this(start, end, step, reverse, false);
    }

    @SuppressWarnings("unchecked")
    public AbstractDataPointCreator(T start, T end, Step<T> step, boolean reverse, boolean cycle) {
        if (start == null && end == null) {
            throw new IllegalArgumentException("start or end must be specified");
        }

        if (end != null && start != null && start.compareTo(end) > 0) {
            throw new IllegalArgumentException("start must be ahead of end ");
        }

        if (reverse && end == null) {
            throw new IllegalArgumentException("When backward generate,end cannot be null but start can be null");
        } else if (!reverse && start == null) {
            throw new IllegalArgumentException("When forward generate,start cannot be null but end can be null");
        }
        //开始时间或者结束时间有一个为空 就表示可以无限生成后续的时间点
        this.reverse = reverse;
        this.start = start;
        this.end = end;
        this.step = step;
        this.cycle = cycle;
    }

    @Nonnull
    @Override
    public Iterator<T> iterator() {
        return new DataPointIterator();
    }

    interface Relay<E> {
        E next();

        boolean hasNext();
    }

    @SuppressWarnings("unchecked")
    private boolean isBefore(T t1, T t2) {
        return t1.compareTo(t2) < 0;
    }
    @SuppressWarnings("unchecked")
    private boolean isAfter(T t1, T t2) {
        return t1.compareTo(t2) > 0;
    }

    //线程安全比较难支持 hasNext 虽然可以做同步 但当两个线程同事访问hasNext都返回true,其中一个线程调用next()成功但是另外一个调用的话 就会抛异常!
    // 要支持的话 1.增加一个 tryNext方法-即当没有元素的时候不抛异常/或者是将异常suppress掉 2.hasNext做条件等待 需要等到前面的线程/任务执行完对应的next操作 才能继续下去
    // 0. 让调用者自己解决竞争/线程安全问题?
    class Forward implements Relay<T> {
        /*volatile*/ T last;

        public boolean hasNext() {
            return last == null
                    || (cycle || end == null)
                    || !isAfter(nextData(), end);
        }

        public T next() {
            return last = (last == null ? start : nextData());
        }

        public T nextData() {
            T next = step.forward(last);
            if (cycle && end != null && isAfter(next, end)) {
                next = start;
            }
            return next;
        }
    }

    class Backward implements Relay<T> {
        /*volatile*/ T last;

        public boolean hasNext() {
            return last == null
                    || (cycle || start == null)
                    || !isBefore(nextData(), start);
        }

        public T next() {
            return last = (last == null ? end : nextData());
        }

        public T nextData() {
            T next = step.backward(last);
            //如果是循环释出 并且下一个点超出起点 回归到end点
            if (cycle && start != null && isBefore(next, start)) {
                next = end;
            }
            return next;
        }
    }


    class DataPointIterator implements Iterator<T> {
        final Relay<T> relay;

        public DataPointIterator() {
            this.relay = (reverse ? new Backward() : new Forward());
        }

        @Override
        public boolean hasNext() {
            return relay.hasNext();
        }

        @Override
        public T next() {
            if (!relay.hasNext()) {
                throw new NoSuchElementException("Cannot generate date point anymore");
            }
            return relay.next();
        }
    }

}