package xyz.asitanokibou.common.lang.lambda;

import xyz.asitanokibou.common.lang.utils.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.function.Function;

public class  SimpleTest {

    static class UserInfo{
        String fullName;
    }

    @Test
    public void testCast(){
        Function<UserInfo, String> fullNameFunc = (Function<UserInfo,String> & Serializable) ui -> ui.fullName;
        String serialFunc = new String(SerializationUtils.serialize(fullNameFunc));
        System.out.println(serialFunc);

        Function<UserInfo, String> fullNameFuncNotSerializable = ui -> ui.fullName;
        try {
            SerializationUtils.serialize(fullNameFuncNotSerializable);
            Assertions.fail();
        } catch (IllegalArgumentException e) {
            System.out.println("不能序列化lambda表达式");
        }
//        String noSerialFunc = new String(SerializationUtils.serialize(fullNameFuncNotSerializable));
//        System.out.println(noSerialFunc);
    }

    @Test
    public void testRandom(){
//        ThreadLocalRandom.current().nextDouble(2, 2);
//        IntStream.ra?

        double[] thresholdNums = new double[2];
        for (double thresholdNum : thresholdNums) {
            System.out.println(thresholdNum);
        }
    }
}
