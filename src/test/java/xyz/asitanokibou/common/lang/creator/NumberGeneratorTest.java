package xyz.asitanokibou.common.lang.creator;

import org.junit.jupiter.api.Test;
import xyz.asitanokibou.common.lang.creator.NumberGenerator;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class NumberGeneratorTest {

    public static final int TEN_THOUSAND_TIMES = 10_000;
    public static final int MILLION_TIMES      = 1_000_000;
//    public static final int HUNDRED_MILLION_TIMES      = 100_000_000;
//    public static final int BILLION_TIME = 1_000_000_000;

    private void testGenerate(NumberGenerator generator, int times, Consumer<Double> numberAction) {
        for (int i = 0; i < times; i++) {
            double next = generator.next();
            numberAction.accept(next);
        }
    }

    private NumberGenerator.Builder generatorBuilder() {
        return NumberGenerator.builder();
    }

    @Test
    void testCreateWithOneValueRange_Open() {
        //前后开区间 (..]
        NumberGenerator generator = generatorBuilder().greaterThan(0).lessThan(10).build(); // (0,10)
        testGenerate(generator, MILLION_TIMES, next ->
                assertTrue(next > 0 && next < 10, getErrorMessage(next)));

        //test with range
        generator = generatorBuilder().range(0, false, 10, false).build();
        testGenerate(generator, MILLION_TIMES, next ->
                assertFalse(next <= 0 || next >= 10, getErrorMessage(next)));

        assertEquals(generator.min(),1.0d);
        assertEquals(generator.max(),9.0d);
    }

    @Test
    void testCreateWithOneValueRange_OpenClose() {
        //前开后闭  [..)
        NumberGenerator generator = generatorBuilder().greaterThan(0).lessEquals(10).build(); // (0,10]
        testGenerate(generator, TEN_THOUSAND_TIMES, next ->
                assertFalse(next <= 0 || next > 10, getErrorMessage(next)));
    }

    @Test
    void testCreateWithOneValueRange_CloseOpen() {
        //前闭后开  [..)
        NumberGenerator generator = generatorBuilder().greaterEquals(0).lessThan(10).build(); // (0,10]
        testGenerate(generator, TEN_THOUSAND_TIMES, next ->
                assertFalse(next < 0 || next >= 10, getErrorMessage(next)));
    }

    @Test
    void testCreateWithOneValueRange_Close() {
        //闭合 [..]
        NumberGenerator generator = generatorBuilder().greaterEquals(0).lessEquals(10).build(); // (0,10]
        testGenerate(generator, TEN_THOUSAND_TIMES, next ->
                assertFalse(next < 0 || next > 10, getErrorMessage(next)));
    }

    @Test
    void testCreateWithOneValueRange_Close_TwoFraction() {
        //闭合 [..] .2
        NumberGenerator generator = generatorBuilder().greaterEquals(0).lessEquals(10).fraction(2).build(); // (0,10]
        testGenerate(generator, TEN_THOUSAND_TIMES, next -> {
            System.out.println(next);
            assertFalse(next < 0 || next > 10, getErrorMessage(next));
        });
    }


    /////////////////////////////////////////////////////////////////////////////////////////////


    @Test
    void testCreatePositiveFraction1Value() {
        // 两个区间 <<<<<<8.0)....(9.0>>>>>>
        NumberGenerator generator = generatorBuilder().lessThan(8).greaterThan(9).fraction(1).positive().build();
        testGenerate(generator, TEN_THOUSAND_TIMES, next ->
                assertFalse(next == 8 || next == 9 || next < 0, getErrorMessage(next)));

        //maxDeviate=100
//        System.out.println(generator.min());
//        System.out.println(generator.max());
        assertEquals(generator.min(),0d);
        assertEquals(generator.max(),(9.0d + 100));// 10 + 100 - 1 ,e.g 10,11位两个数, [10..109] 100个数

        // 两个区间 [0,<<<<<<8.00)....(9.00>>>>>>
        generator = generatorBuilder().lessThan(8).greaterThan(9).fraction(2).positive().build();
        testGenerate(generator, TEN_THOUSAND_TIMES, next ->
                assertFalse(next == 8 || next == 9 || next < 0, getErrorMessage(next)));
    }

    @Test
    void testCreateValueWithEquals(){
        NumberGenerator generator = generatorBuilder().notEquals(10.3).positive().build();
        testGenerate(generator,MILLION_TIMES,next ->
                assertNotEquals(10.3, next, 0.0, getErrorMessage(next)));

        generator = generatorBuilder().equals(10.3).positive().build();
        testGenerate(generator,MILLION_TIMES,next ->
                assertEquals(10.3, next, 0.0, getErrorMessage(next)));
    }

    @Test
    void testCreateValueWithSpecifiedMaxDeviate(){
        NumberGenerator generator = generatorBuilder()
                .lessEquals(80).greaterEquals(90).maxDeviate(10).build();
        testGenerate(generator,MILLION_TIMES,next ->
                assertTrue((70 <= next && next <= 80)
                                || (90 <= next && next <= 100) , getErrorMessage(next)));

    }


    private String getErrorMessage(Double next) {
        return next + " is not a expected number";
    }

}