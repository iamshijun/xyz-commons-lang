package xyz.asitanokibou.common.lang.lambda;

import xyz.asitanokibou.common.lang.model.Foo;
import xyz.asitanokibou.common.lang.model.LogicDeletable;
import xyz.asitanokibou.common.lang.utils.SerializationUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class LambdaUtilsTest {

    @Test
    void testResolveWithMybatisPlusSerializedLambda() {
        System.out.println("---------------------testResolveWithMybatisPlusSerializedLambda----------------------------");
        SFunction<Foo, ?> func = LogicDeletable::getDelFlag;
        SerializedLambda serializedLambda = SerializedLambda.resolve(func);

        String implMethodName = serializedLambda.getImplMethodName();
        String implClassName = serializedLambda.getImplClassName();
        System.out.println("implClassName : " + implClassName);
        System.out.println("implMethodName : " + implMethodName);

    }

    @Test
    void testResolveWithJdkSerializedLambda() {
        System.out.println("----------------------testResolveWithJdkSerializedLambda---------------------------");
        SFunction<Foo, ?> func = Foo::getDelFlag;
        Object serializedLambda = LambdaUtils.resolve(func);
//        String implMethodName = serializedLambda.getImplMethodName();
//        System.out.println(implMethodName);
        System.out.println(serializedLambda.getClass());
    }


    @Test
    void testSerialize(){
        System.out.println("-----------------------testSerialize--------------------------");
        SFunction<Foo, ?> func = Foo::getName;
        byte[] serialize = SerializationUtils.serialize(func);
        System.out.println(Arrays.toString(serialize));
        System.out.println(new String(serialize));
    }

}