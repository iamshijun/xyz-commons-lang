package xyz.asitanokibou.common.lang.creator;

import org.junit.jupiter.api.Test;

public class DoubleCreatorTest {

    @Test
    void testVersaDoubleStream() {
        DoubleCreator doubleCreator = new DoubleCreator(1.67d, 10d, 0.1d);
        printTop(doubleCreator, 20);
    }
    private void printTop(DoubleCreator creator, int top) {
        creator.stream()
                .limit(top)
                .forEach(this::println);
    }

    private void println(Object value) {
        System.out.println(value);
    }

}
