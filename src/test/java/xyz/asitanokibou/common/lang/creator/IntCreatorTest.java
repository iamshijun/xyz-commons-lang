package xyz.asitanokibou.common.lang.creator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IntCreatorTest {


    private void println(Object value) {
        System.out.println(value);
    }


    @Test
    void testVersaIntStream(){
        IntegerCreator intCreator = new IntegerCreator(1,10,1);
        IntStream intStream = IntStream.rangeClosed(1, 10);

        List<Integer> createInts = intCreator.stream().collect(Collectors.toList());
        List<Integer> streamInts = intStream.boxed().collect(Collectors.toList());

        Assertions.assertIterableEquals(createInts, streamInts);

        printTop(intCreator,20);
    }

    @Test
    void testOffset(){
        IntegerCreator integers = new IntegerCreator(1,13,4,true);
        integers.stream().skip(3).limit(10).forEach(this::println);
    }

    @Test
    void testNoEnd(){
        IntegerCreator integers = new IntegerCreator(1,null);
        //printTop(integers,10);
        integers.stream().skip(12).limit(30).forEach(this::println);
    }

    @Test
    void testNoStart(){
        IntegerCreator integers = new IntegerCreator(null,100,1,true,false);
        //printTop(integers,10);
        integers.stream().skip(12).limit(30).forEach(this::println);
    }

    @Test
    void testReverse(){
        IntegerCreator integers = new IntegerCreator(1,13,1,true,false);
        printTop(integers,10);
    }

    private void printTop(IntegerCreator creator,int top) {
        creator.stream()
                .limit(top)
                .forEach(this::println);
    }
}
