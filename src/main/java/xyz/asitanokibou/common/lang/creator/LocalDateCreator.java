package xyz.asitanokibou.common.lang.creator;

import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.Period;

/**
 * @author aimysaber@gmail.com
 */
public class LocalDateCreator extends AbstractDataPointCreator<LocalDate> {

    public LocalDateCreator(Builder builder) {
        this(builder.startDate, builder.endDate, builder.interval, builder.reverse, builder.cycle);
    }

    public static Builder builder() {
        return new LocalDateCreator.Builder();
    }

    public LocalDateCreator(LocalDate startDate, LocalDate endDate, Period interval) {
        this(startDate, endDate, interval, false);
    }

    public LocalDateCreator(LocalDate startDate, LocalDate endDate,
                            Period interval,
                            boolean reverse) {
        this(startDate, endDate, interval, reverse, false);
    }


    public static LocalDateCreator create(LocalDate startDate, LocalDate endDate,
                                          Period interval, boolean reverse, boolean cycle) {
        return new LocalDateCreator(startDate, endDate, interval, reverse, cycle);
    }

    public static LocalDateCreator create(LocalDate startDate, LocalDate endDate, Period interval, boolean reverse) {
        return new LocalDateCreator(startDate, endDate, interval, reverse);
    }

    public static LocalDateCreator create(LocalDate startDate, LocalDate endDate, Period interval) {
        return new LocalDateCreator(startDate, endDate, interval, false);
    }

    public static LocalDateCreator forward(LocalDate startDate, Period interval) {
        return new LocalDateCreator(startDate, null, interval, false);
    }

    public static LocalDateCreator backward(LocalDate endDate, Period interval) {
        return new LocalDateCreator(null, endDate, interval, true);
    }

    @AllArgsConstructor
    private static class LocalDatePeriodStep implements Step<LocalDate> {
        final Period period;//

        @Override
        public LocalDate forward(@Nonnull LocalDate current) {
            return current.plus(period);
        }

        @Override
        public LocalDate backward(@Nonnull LocalDate current) {
            return current.minus(period);
        }
    }

//    private Duration interval;

    public LocalDateCreator(LocalDate startDate, LocalDate endDate,
                            Period interval,
                            boolean reverse,
                            boolean cycle) {
        super(startDate, endDate, new LocalDatePeriodStep(interval), reverse, cycle);
//        this.interval = interval;
    }

    public static class Builder {

        private LocalDate startDate;
        private LocalDate endDate;
        private Period interval;
        private boolean reverse;
        private boolean cycle;

        public Builder date(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder interval(Period interval) {
            this.interval = interval;
            return this;
        }

        public Builder interval(String interval) {
            return interval(Period.parse(interval));
        }
        public Builder daily() {
            return interval(Period.ofDays(1));
        }
        public Builder monthly() {
            return interval(Period.ofMonths(1));
        }
        public Builder annually() {
            return interval(Period.ofYears(1));
        }

        public Builder reverse() {
            this.reverse = true;
            return this;
        }

        public Builder cycle() {
            this.cycle = true;
            return this;
        }

        public LocalDateCreator build() {
            //validate the arguments
            if (startDate == null && endDate == null) {
                throw new IllegalArgumentException("either startDate or endDate should be specified");
            }
            return new LocalDateCreator(this);
        }
    }
}
