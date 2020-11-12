package xyz.asitanokibou.common.lang.creator;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 时间生成器 - 根据指定的开始,结束时间和间隔产生时间 TODO 增加线程安全的支持
 * @deprecated
 * @author shijun.shi
 */
//legacy
public class DatePointCreator implements ValueCreator<LocalDateTime> {

    /**
     * 产生数据时间的 开始时间点
     */
    private final LocalDateTime startTime;
    /**
     * 产生数据时间的 开始时间点
     */
    private final LocalDateTime endTime;
    /**
     * 产生的时间点之间的间隔
     */
    private final Duration interval;
    /**
     * 是否反向生成时间点
     */
    private final boolean reverse;
    /**
     * 是否循环产生时间点 - 即到达最后时间点endTime后下一个时间点重新从startTime开始 持续生成时间点 hasNext永远为true
     */
    private final boolean cycle;

//    private boolean threadSafe;

    public DatePointCreator(Builder builder) {
        this(builder.startTime, builder.endTime, builder.interval, builder.reverse, builder.cycle);
    }
    public static Builder builder(){
        return new Builder();
    }


    public DatePointCreator(LocalDateTime startTime, LocalDateTime endTime, Duration interval) {
        this(startTime, endTime, interval, false);
    }

    public DatePointCreator(LocalDateTime startTime, LocalDateTime endTime,
                            Duration interval,
                            boolean reverse) {
        this(startTime, endTime, interval, reverse, false);
    }

    public DatePointCreator(LocalDateTime startTime, LocalDateTime endTime,
                            Duration interval,
                            boolean reverse,
                            boolean cycle) {
        if (startTime == null && endTime == null) {
            throw new IllegalArgumentException("startTime or endTime must be specified");
        }

        if (endTime != null && startTime != null && startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("startTime must be ahead of endTime ");
        }

        if (reverse && endTime == null) {
            throw new IllegalArgumentException("When backward generate,endTime cannot be null but startTime can be null");
        } else if (!reverse && startTime == null) {
            throw new IllegalArgumentException("When forward generate,startTime cannot be null but endTime can be null");
        }
        //开始时间或者结束时间有一个为空 就表示可以无限生成后续的时间点
        this.reverse = reverse;
        this.startTime = startTime == null ? null : startTime.withNano(0);
        this.endTime = endTime == null ? null : endTime.withNano(0);
        this.interval = interval;
        this.cycle = cycle;
    }

    public static DatePointCreator create(LocalDateTime startTime, LocalDateTime endTime,
                                          Duration interval, boolean reverse, boolean cycle) {
        return new DatePointCreator(startTime, endTime, interval, reverse, cycle);
    }

    public static DatePointCreator create(LocalDateTime startTime, LocalDateTime endTime, Duration interval, boolean reverse) {
        return new DatePointCreator(startTime, endTime, interval, reverse);
    }

    public static DatePointCreator create(LocalDateTime startTime, LocalDateTime endTime, Duration interval) {
        return new DatePointCreator(startTime, endTime, interval, false);
    }

    public static DatePointCreator forward(LocalDateTime startTime, Duration interval) {
        return new DatePointCreator(startTime, null, interval, false);
    }

    public static DatePointCreator backward(LocalDateTime endTime, Duration interval) {
        return new DatePointCreator(null, endTime, interval, true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatePointCreator dateTimes = (DatePointCreator) o;
        return reverse == dateTimes.reverse &&
                cycle == dateTimes.cycle &&
                startTime.equals(dateTimes.startTime) &&
                endTime.equals(dateTimes.endTime) &&
                interval.equals(dateTimes.interval);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime, interval, reverse, cycle);
    }

    @Nonnull
    @Override
    public Iterator<LocalDateTime> iterator() {
        return new DatePointIterator();
    }

//    public DatePointIterator exposeIterator() {
//        return new DatePointIterator();
//    }

    interface Relay {
        LocalDateTime next();
        boolean hasNext();
    }

    //线程安全比较难支持 hasNext 虽然可以做同步 但当两个线程同事访问hasNext都返回true,其中一个线程调用next()成功但是另外一个调用的话 就会抛异常!
    // 要支持的话 1.增加一个 tryNext方法-即当没有元素的时候不抛异常/或者是将异常suppress掉 2.hasNext做条件等待 需要等到前面的线程/任务执行完对应的next操作 才能继续下去
    // 0. 让调用者自己解决竞争/线程安全问题?
    class ForwardTime implements Relay {
        /*volatile*/ LocalDateTime lastTime;

        public boolean hasNext() {
            return lastTime == null || cycle || endTime == null || !nextData().isAfter(endTime);
        }

        public LocalDateTime next() {
            return lastTime = lastTime == null ? startTime : nextData();
        }

        public LocalDateTime nextData() {
            LocalDateTime nextData =  lastTime.plus(interval);
            if (cycle && endTime != null && nextData.isAfter(endTime)) {
                nextData = startTime;
            }
            return nextData;
        }
    }

    class BackwardTime implements Relay {
        /*volatile*/ LocalDateTime lastTime;

        public boolean hasNext() {
            return lastTime == null || cycle || startTime == null || !nextData().isBefore(startTime);
        }

        public LocalDateTime next() {
            //正常来说如果没有下一个
            return lastTime = lastTime == null ? endTime : nextData();
        }

        public LocalDateTime nextData() {
            LocalDateTime nextData = lastTime.minus(interval);
            //如果是循环释出 并且下一个点超出开始时间 回归到endTime点
            if(cycle && startTime != null && nextData.isBefore(startTime)) {
                nextData = endTime;
            }
            return nextData;
        }
    }

    /**
     * 时间生成器内部的实际迭代器
     * */
    class DatePointIterator implements Iterator<LocalDateTime> {
        final Relay relay;

        public DatePointIterator() {
            this.relay = reverse ? new BackwardTime() : new ForwardTime();
        }

        @Override
        public boolean hasNext() {
            return relay.hasNext();
        }

        @Override
        public LocalDateTime next() {
            if (!relay.hasNext()) {
                throw new NoSuchElementException("Cannot generate date point anymore");
            }
            return relay.next();
        }

      /*  public LocalDateTime tryNext(){
            if (!relay.hasNext()) {
                return null;
            }
            return relay.next();
        }*/
    }


    /*---- builder  --- */
    public static class Builder {

        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Duration interval;
        private boolean reverse;
        private boolean cycle;

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }
        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }
        public Builder interval(Duration interval){
            this.interval = interval;
            return this;
        }
        public Builder interval(String interval) {
            return interval(Duration.parse(interval));
        }
        public Builder secondly() {
            return interval(Duration.ofSeconds(1));
        }
        public Builder minutely() {
            return interval(Duration.ofMinutes(1));
        }
        public Builder hourly() {
            return interval(Duration.ofHours(1));
        }
        public Builder daily() {
            return interval(Duration.ofDays(1));
        }

        public Builder reverse(){
            this.reverse = true;
            return this;
        }

        public Builder cycle(){
            this.cycle = true;
            return this;
        }

        public DatePointCreator build(){
            //validate the arguments
            if (startTime == null && endTime == null) {
                throw new IllegalArgumentException("either startTime or endTime should be specified");
            }
            return new DatePointCreator(this);
        }
    }

}