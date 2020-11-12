package xyz.asitanokibou.common.lang.utils;

public final class ClassUtils {

    public static Class<?> toClassConfident(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到指定的class！请仅在明确确定会有 class 的时候，调用该方法", e);
        }
    }
}
