package xyz.asitanokibou.common.lang.collection;

import lombok.Data;
import org.junit.jupiter.api.Test;
import xyz.asitanokibou.common.lang.creator.LocalDateTimeCreator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Iterator;

public class LatestMapTest {

    @Test
    void testLatestDateTimeMap() {
        LocalDate date = LocalDate.of(2020, 1, 1);
        LocalTime startTime = LocalTime.of(1, 0, 0);
        LocalTime endTime   = LocalTime.of(2, 0, 0);

        LocalDateTimeCreator creator = LocalDateTimeCreator.builder()
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .secondly().build();

//        Iterator<LocalTime> timeIterator = creator.timeIterator();
        Iterator<LocalDateTime> timeIterator = creator.iterator();

        int retainSeconds = 10;
        LatestDataTimeMap<LocalDateTimeAwareBean> latestDataTimeMap =
                LatestDataTimeMap.create(retainSeconds,LocalDateTimeAwareBean::getTime);

        int num = 0;
        while(timeIterator.hasNext()){
            num++;
            LocalDateTime time = timeIterator.next();
            LocalDateTimeAwareBean bean = new LocalDateTimeAwareBean(num, time);
            latestDataTimeMap.add(bean);

            if (num % retainSeconds == 0) {
                System.out.println(latestDataTimeMap);
            }
        }
    }

    @Data
    private static class LocalDateTimeAwareBean implements TimeAware<LocalDateTime> {
        private final int value;
        private final LocalDateTime time;

        @Override
        public LocalDateTime getTime() {
            return time;
        }
    }
    public interface TimeAware<T> {
        T getTime();
    }

}
