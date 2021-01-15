package xyz.asitanokibou.common.lang;

import org.junit.jupiter.api.Test;
import xyz.asitanokibou.common.lang.vo.Bar;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class SpecTest {

    @Test
    void testLogicOp(){
        int x = 2;
        System.out.println(1 >= x);
        System.out.println(x < 3);
    }

    @Test
    void testComparator(){
        Bar bar = new Bar();
        List<Bar> bars = new ArrayList<>();
        bars.sort(Comparator.comparing(Bar::getNumber));

        Function<Bar, Integer> fun = Bar::getNumber;
        Consumer<Bar> cons = bar::getNumber;
    }
}
