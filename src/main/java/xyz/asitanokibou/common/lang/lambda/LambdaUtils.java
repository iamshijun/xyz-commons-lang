package xyz.asitanokibou.common.lang.lambda;

import xyz.asitanokibou.common.lang.utils.SerializationUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class LambdaUtils {

    /**
     * 通过反序列化转换 lambda 表达式，该方法只能序列化 lambda 表达式，不能序列化接口实现或者正常非 lambda 写法的对象
     *
     * @param lambda lambda对象
     * @return 返回解析后的 SerializedLambda
     */
    public static Object resolve(SFunction lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw new RuntimeException("该方法仅能传入 lambda 表达式产生的合成类");
        }
        final byte[] serialize = SerializationUtils.serialize(lambda);
        System.out.println(new String(serialize));
        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(serialize)) {
            @Override
            protected Class<?> resolveClass(ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
                Class<?> clazz = super.resolveClass(objectStreamClass);
                System.out.println(clazz);
                return clazz;
//                return clazz == java.lang.invoke.SerializedLambda.class ? SerializedLambda.class : clazz;
            }
        }) {
            //读取对象
            return objIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            //throw ExceptionUtils.mpe("This is impossible to happen", e);
            throw new RuntimeException("This is impossible to happen", e);
        }
    }


}
