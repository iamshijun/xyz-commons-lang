package xyz.asitanokibou.common.lang.utils;

public final class NumberUtils {

    public static int fraction(double number) {
        String valueOf = Double.toString(number);
        if (valueOf.contains(".")) {
            //new BigDecimal(valueOf).stripTrailingZeros().toPlainString()
            return valueOf
                    .replaceFirst("\\d+\\.", "")
                    .replaceFirst("0+$", "")
                    .length()
                    ;
        }
        return 0;
    }
}
