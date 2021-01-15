package xyz.asitanokibou.common.lang.collection;

import xyz.asitanokibou.common.lang.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class LatestDataTimeMap<E> extends AbstractLatestMap<E,LocalDateTime> {

    private static final long DEFAULT_SECONDS = TimeUnit.MINUTES.toSeconds(1);

    public static <E> LatestDataTimeMap<E> create(Function<E,LocalDateTime> timeExtractor){
        return new LatestDataTimeMap<>(DEFAULT_SECONDS,timeExtractor);
    }

    public static <E> LatestDataTimeMap<E> create(long seconds,Function<E,LocalDateTime> timeExtractor){
        return new LatestDataTimeMap<>(seconds,timeExtractor);
    }

    private LatestDataTimeMap(long seconds,Function<E,LocalDateTime> timeExtractor) {
        super(seconds, timeExtractor, new TimeToSecondHashing(seconds));
    }

    private static class TimeToSecondHashing implements Function<LocalDateTime,Long> {
        public final long retainSize;

        TimeToSecondHashing(long retainSize) {
            this.retainSize = retainSize;
        }
        @Override
        public Long apply(LocalDateTime time) {
            return DateUtils.toEpochSecond(time) % retainSize;
        }
    }
}
