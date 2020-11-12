package xyz.asitanokibou.common.lang.creator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.asitanokibou.common.lang.creator.DatePointCreator;
import xyz.asitanokibou.common.lang.creator.LocalDateCreator;
import xyz.asitanokibou.common.lang.creator.LocalDateTimeCreator;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class DatePointCreatorTest {

    public static final DateTimeFormatter dateTimeFormatter
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter dateFormatter
            = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String format(LocalDate localDate) {
        return localDate.format(dateFormatter);
    }

    private String format(LocalDateTime localDateTime) {
        return localDateTime.format(dateTimeFormatter);
    }

    private void println(Object value) {
        System.out.println(value);
    }

    @Test
    void testCycleTime() {
        LocalDate date = LocalDate.of(2020, 1, 1);
        LocalTime time = LocalTime.of(1, 0, 1);

        LocalDateTime startDateTime = LocalDateTime.of(date, time);
        LocalDateTime endDateTime = startDateTime.plus(Duration.ofSeconds(4));

        DatePointCreator datePointCreator =
                DatePointCreator.create(startDateTime, endDateTime, Duration.ofSeconds(1), false, true);

        LocalDateTimeCreator localDateTimeCreator =
                LocalDateTimeCreator.create(startDateTime, endDateTime, Duration.ofSeconds(1), false, true);

        List<String> legacyCreatorDataPoints = datePointCreator.stream()
                .limit(10).map(this::format).collect(Collectors.toList());
        List<String> localDateTimeCreatorDataPoints = localDateTimeCreator.stream()
                .limit(10).map(this::format).collect(Collectors.toList());

        System.out.println(legacyCreatorDataPoints);
        Assertions.assertIterableEquals(legacyCreatorDataPoints, localDateTimeCreatorDataPoints);

        //System.out.println(localDateTimeCreatorDataPoints);
    }

    @Test
    void testCycleWithLocalDateCreator() {
        LocalDate startDate = LocalDate.of(2020, 1, 23);
        LocalDate endDate = LocalDate.of(2020, 5, 22);

        int months = Period.between(startDate, endDate).getMonths();
        //System.out.println(months);
        if (startDate.getDayOfMonth() != endDate.getDayOfMonth()) {
            months += 1;
        }
        //System.out.println(months);

        LocalDateCreator dateCreatorByBuilder = LocalDateCreator.builder()
                .date(startDate, endDate).cycle().monthly().build();

        List<LocalDate> dates1 =
                dateCreatorByBuilder.stream().limit(months).collect(Collectors.toList());
        List<LocalDate> dates2 =
                dateCreatorByBuilder.stream().skip(months).limit(months).collect(Collectors.toList());

        Assertions.assertIterableEquals(dates1, dates2);

        dates2.forEach(this::println);
    }

    @Test
    void testCycleTimeWithBuilder() {
        LocalDate date = LocalDate.of(2020, 1, 1);
        LocalTime time = LocalTime.of(1, 0, 1);

        LocalDateTime startDateTime = LocalDateTime.of(date, time);
        LocalDateTime endDateTime = startDateTime.plus(Duration.ofSeconds(4));

        DatePointCreator datePointCreatorWithBuild = DatePointCreator.builder()
                .startTime(startDateTime)
                .endTime(endDateTime)
                .secondly()
                .cycle()
                .build();

        DatePointCreator datePointCreator =
                DatePointCreator.create(startDateTime, endDateTime, Duration.ofSeconds(1), false, true);

        LocalDateTimeCreator localDateTimeCreator =
                LocalDateTimeCreator.create(startDateTime, endDateTime, Duration.ofSeconds(1), false, true);

        Assertions.assertEquals(datePointCreator, datePointCreatorWithBuild);

        List<LocalDateTime> dateTimeProduced = datePointCreatorWithBuild.stream().limit(10).collect(Collectors.toList());
        List<LocalDateTime> dateTimeProduced2 = datePointCreator.stream().limit(10).collect(Collectors.toList());

        List<LocalDateTime> localDateTimeCreatorPoints
                = localDateTimeCreator.stream().limit(10).collect(Collectors.toList());

        Assertions.assertIterableEquals(dateTimeProduced, dateTimeProduced2);
        Assertions.assertIterableEquals(dateTimeProduced2, localDateTimeCreatorPoints);

        dateTimeProduced.stream().map(this::format).forEach(System.out::println);
    }

    @Test
    void testCycleTime2() {
        LocalDate date = LocalDate.of(2020, 1, 1);
        LocalTime time = LocalTime.of(1, 0, 0);

        LocalDateTime startDateTime = LocalDateTime.of(date, time);
        LocalDateTime endDateTime = startDateTime.plus(Duration.ofMinutes(1).minusSeconds(1));

        DatePointCreator datePointCreator =
                DatePointCreator.create(startDateTime, endDateTime, Duration.ofSeconds(1), false, true);

        long secondsInMinute = Duration.ofMinutes(1).getSeconds();

        List<LocalDateTime> collectData1 = datePointCreator.stream()
                .limit(secondsInMinute)
                .collect(Collectors.toList());

        List<LocalDateTime> collectData2 = datePointCreator.stream()
                .skip(secondsInMinute)
                .limit(secondsInMinute)
                .collect(Collectors.toList());

        Assertions.assertIterableEquals(collectData1, collectData2);
    }

    @Test
    void testCreateLocalDate() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate endDate = LocalDate.of(2020, 10, 5);

//        startDate.plus(Period.ofDays(1));
        //not permitted
//        startDate.plus(Duration.ofDays(1));
//        startDate.plus(Duration.ofMinutes(1));

        LocalDateCreator dateCreatorByBuilder = LocalDateCreator.builder()
                .date(startDate, endDate)
//                .cycle()
                .monthly()
                .build();

        LocalDateCreator datePointCreator =
                LocalDateCreator.create(startDate, endDate, Period.ofMonths(1), false, false);
////
        List<LocalDate> dateCreated = dateCreatorByBuilder.stream()
                .limit(10).collect(Collectors.toList());

        List<LocalDate> dateCreated2 = datePointCreator.stream()
                .limit(10).collect(Collectors.toList());

        Assertions.assertIterableEquals(dateCreated, dateCreated2);

        dateCreated.stream().map(this::format).forEach(this::println);

    }

    @Test
    void testIteratorMultipleTime() {
        LocalDateTime now = LocalDateTime.now();
        DatePointCreator dateTimeCreator =
                DatePointCreator.create(null, now, Duration.ofHours(1), true);

        Iterator<LocalDateTime> iterator = dateTimeCreator.iterator();
        final int max = 2;

        int times = max;
        List<LocalDateTime> result1 = new ArrayList<>(max);
        while (iterator.hasNext() && --times >= 0) {
            result1.add(iterator.next());
        }

        times = max;
        List<LocalDateTime> result2 = new ArrayList<>(max);
        Iterator<LocalDateTime> iterator2 = dateTimeCreator.iterator();
        while (iterator2.hasNext() && --times >= 0) {
            result2.add(iterator2.next());
        }

        Assertions.assertIterableEquals(result1, result2);
    }

    @Test
    void testStream() {
        DatePointCreator.forward(LocalDateTime.now(), Duration.ofSeconds(8))
                .stream()
                .skip(5)
                .limit(300)
                .map(this::format)
                .forEach(this::println)
        ;
    }

    @Test
    void testStream2() {
        DatePointCreator.backward(LocalDateTime.now(), Duration.ofSeconds(1))
                .stream()
                .skip(5)
                .limit(300)
                .collect(Collectors.groupingBy(LocalDateTime::getMinute))
                .forEach((k, v) -> {
                    System.out.println(k);
                    System.out.println("\t" + v);
                });
    }
}