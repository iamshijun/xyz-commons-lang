package xyz.asitanokibou.common.lang.creator;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.annotation.Nonnull;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.*;

/**
 * @author aimysaber@gmail.com
 */
public class LocalDateTimeCreator extends AbstractDataPointCreator<LocalDateTime> {

    public LocalDateTimeCreator(Builder builder) {
        this(builder.startDateTime, builder.endDateTime, builder.interval, builder.reverse, builder.cycle);
    }
    public static Builder builder(){
        return new LocalDateTimeCreator.Builder();
    }

    public LocalDateTimeCreator(LocalDateTime startTime, LocalDateTime endTime, Duration interval) {
        this(startTime, endTime, interval, false);
    }

    public LocalDateTimeCreator(LocalDateTime startTime, LocalDateTime endTime,
                                Duration interval,
                                boolean reverse) {
        this(startTime, endTime, interval, reverse, false);
    }

    public static LocalDateTimeCreator create(LocalDate date, LocalTime startTime, LocalTime endTime,
                                              Duration interval, boolean reverse, boolean cycle) {
        return new LocalDateTimeCreator(LocalDateTime.of(date,startTime), LocalDateTime.of(date,endTime), interval, reverse, cycle);
    }


    public static LocalDateTimeCreator create(LocalDateTime startTime, LocalDateTime endTime,
                                              Duration interval, boolean reverse, boolean cycle) {
        return new LocalDateTimeCreator(startTime, endTime, interval, reverse, cycle);
    }

    public static LocalDateTimeCreator create(LocalDateTime startTime, LocalDateTime endTime, Duration interval, boolean reverse) {
        return new LocalDateTimeCreator(startTime, endTime, interval, reverse);
    }

    public static LocalDateTimeCreator create(LocalDateTime startTime, LocalDateTime endTime, Duration interval) {
        return new LocalDateTimeCreator(startTime, endTime, interval, false);
    }

    public static LocalDateTimeCreator forward(LocalDateTime startTime, Duration interval) {
        return new LocalDateTimeCreator(startTime, null, interval, false);
    }

    public static LocalDateTimeCreator backward(LocalDateTime endTime, Duration interval) {
        return new LocalDateTimeCreator(null, endTime, interval, true);
    }

    @Data
    @AllArgsConstructor
    private static class LocalDateTimeDurationStep implements Step<LocalDateTime> {
        final TemporalAmount duration;
        @Override
        public LocalDateTime forward(@Nonnull LocalDateTime current) {
            return current.plus(duration);
        }
        @Override
        public LocalDateTime backward(@Nonnull LocalDateTime current) {
            return current.minus(duration);
        }
    }

//    private Duration interval;

    public LocalDateTimeCreator(LocalDateTime startTime, LocalDateTime endTime,
                                TemporalAmount interval,
                                boolean reverse,
                                boolean cycle) {
        super(startTime, endTime, new LocalDateTimeDurationStep(interval), reverse, cycle);
//        this.interval = interval;
    }

    @Nonnull
    public Iterator<LocalTime> timeIterator() {
        Iterator<LocalDateTime> iterator = super.iterator();
        return new Iterator<LocalTime>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public LocalTime next() {
                return iterator.next().toLocalTime();
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalDateTimeCreator otherCreator = (LocalDateTimeCreator) o;
        return reverse == otherCreator.reverse &&
                cycle == otherCreator.cycle &&
                start.equals(otherCreator.end) &&
                end.equals(otherCreator.end) &&
                step.equals(otherCreator.step);
    }

    public static class Builder {

        private LocalDate date;
        private LocalTime startTime;
        private LocalTime endTime;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private TemporalAmount interval;
        private boolean reverse;
        private boolean cycle;

        public Builder startDateTime(LocalDateTime startDateTime) {
            this.startDateTime = startDateTime;
            return this;
        }
        public Builder endDateTime(LocalDateTime endDateTime) {
            this.endDateTime = endDateTime;
            return this;
        }
        public Builder startTime(LocalTime startTime) {
            this.startTime = startTime;
            return this;
        }
        public Builder endTime(LocalTime endTime) {
            this.endTime = endTime;
            return this;
        }
        //暂时不支持同时指定Duration 和 Period , Duration +Period
        public Builder interval(Duration interval){
            this.interval = interval;
            return this;
        }
        public Builder interval(Period interval){
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
        public Builder monthly(){return interval(Period.ofMonths(1));}
        public Builder yearly(){return interval(Period.ofYears(1));}
        public Builder weekly(){return interval(Period.ofWeeks(1));}

        public Builder reverse(){
            this.reverse = true;
            return this;
        }

        public Builder cycle(){
            this.cycle = true;
            return this;
        }

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public LocalDateTimeCreator build(){
            //validate the arguments
            if (startDateTime == null && date != null && startTime != null) {
                startDateTime = LocalDateTime.of(date, startTime);
            }
            if (endDateTime == null  && date != null && endTime != null) {
                endDateTime = LocalDateTime.of(date, endTime);
            }

            if (startDateTime == null && endDateTime == null) {
                throw new IllegalArgumentException("either startTime or endTime should be specified");
            }
            return new LocalDateTimeCreator(this);
        }
    }
}
