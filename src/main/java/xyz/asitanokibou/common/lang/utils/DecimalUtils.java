package xyz.asitanokibou.common.lang.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtils {

    public static void main(String[] args) {

        //testPlainString();
        printPS(BigDecimal.valueOf(0.1234));
        printPS(BigDecimal.valueOf(0.12345));
        printPS(BigDecimal.valueOf(1));
        printPS(BigDecimal.valueOf(0.0001));
        printPS(BigDecimal.valueOf(0.00001));
        printPS(BigDecimal.valueOf(0.0002));
        printPS(BigDecimal.valueOf(0.0011));
        printPS(BigDecimal.valueOf(123.45));//12345 * 10^-2

    }

    private static void printPS(BigDecimal bigDecimal){
        System.out.println(String.format("%-10s - Precision:%d Scale:%s",
                bigDecimal.toPlainString(),bigDecimal.precision(),bigDecimal.scale()));
    }

    private static void testPlainString() {
        String str = "0.000000000000000011138";
        BigDecimal bigDecimal = new BigDecimal(str);
        System.out.println(bigDecimal);
        System.out.println("toPlainString:" + bigDecimal.toPlainString());//不含指数:E

        BigDecimal val1 = BigDecimal.valueOf(0.0001);
        BigDecimal val2 = BigDecimal.valueOf(0.0019);

        System.out.println(val1.scale());
        System.out.println(val1.precision());

        System.out.println(val2.scale());
        System.out.println(val2.precision());

        System.out.println(val1);
        System.out.println(val1.stripTrailingZeros());
        System.out.println(val1.toPlainString());

        BigDecimal result = val1.add(val2).setScale(4, RoundingMode.UP);
        System.out.println(result.toPlainString());
    }

}
