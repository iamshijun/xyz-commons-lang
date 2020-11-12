package xyz.asitanokibou.common.lang.creator;


import lombok.AllArgsConstructor;
import xyz.asitanokibou.common.lang.enums.LogicalType;
import xyz.asitanokibou.common.lang.utils.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author aimysaber@gmail.com
 */
//1. 存在两种范围期间
// 1.1 有界区间 - 共四种情况 [A,B],(A,B),[A,B),(A,B]
// 1.2 无界区间  e.g:  ( (-∞,A],[B,+∞) )
//   1.2.1 这种区间1稍微折衷下 将无穷大做一些限制 (由用户选择或者在原来基数的基础上加/乘一个大值,又或者是int,long的最大值)
//   1.2.2 加上上述区间变为  [i,A] [B,j) 后面主要针对 [i,A]区间做一些转换 (当然也可以不做转换)
//      想象下如果使用 [A,+∞) 生成一个随机数 X 那么可以以A为d对称轴  X-2A 得到在 (-∞,A]上的值，当然如果这里的A不是闭空间要稍作转换

//2. 小数转换为整数
//  - （1）如果Random nextDouble的值 对于是闭空间的最大值B来大说 生成的随机值无法达到这个最大值。 最大值B和可生成的B' 假设相差0.1
// 两个数值之间的可取值范围都是非常巨大的 假设B为10 B‘为9.9 它们之间还有 9.91,9.9101,9.9999,9.999999999 等取值 无限的接近于B
// 所以当前随机数生成 会让用户选择保留/有效的小数位数 来限制上述可能出现任意多的小数位的情况.  并可利用有效数位来将小数转换位整数 -
// 实际就是简单的将小数乘以10^小数位个数.  然后使用 Random的nextInt (所以如果小数位很大的话 可能会让转换后的整数溢出 - 这是该类的主要限制之一，也是最大的缺陷. 所以需要注意小数位 )
// (--解决方案: 应该可以将该数拆分成多分 高位,低位,小数位分别算出后合并..)
//  - (2)
public class NumberGenerator {

    private final ThreadLocalRandom threadLocalRandom;

    private final int fraction;//小数点位数 1.控制返回的数值的位置 2.将start开区间转为闭区间.

    //private final Logger logger = Logger.getLogger(getClass().getName());

    private final ValueRange[] valueRanges;
    private volatile Double min;
    private volatile Double max;

    private NumberGenerator(Builder builder) {
        this.fraction = builder.fraction;
        this.threadLocalRandom = ThreadLocalRandom.current();
        this.valueRanges = builder.valueRanges.toArray(new ValueRange[0]);
    }

    public static Builder builder() {
        return new Builder();
    }

    private static double power10(int fraction) {
        return Math.pow(10, fraction);
    }

    //总是返回有效值范围中的最小值
    public double min(){
        if (min != null) {
            return min;
        }
        ValueRange valueRange = valueRanges[0];
        min = next(valueRange.min, valueRange.min);
        //注意如果是 (-无穷,A)的情况 最小值只能到达指定 maxDeviate的范围 数值不会接近无穷的小(impossible?)
        return min;
    }

    //总是返回有效值范围中的最小值
    public double max(){
        if (max != null) {
            return max;
        }
        ValueRange valueRange;
        if (valueRanges.length > 1) {
            valueRange = valueRanges[valueRanges.length-1];
        }else{
            valueRange = valueRanges[0];
        }
        max = next(valueRange.max - 1, valueRange.max - 1);
        return max;
    }

    //返回一个随机值(在有效值范围中)
    public double next() {
        //将逻辑放在 ValueRange 中! TODO
        int rangeIndex = 0;
        if (valueRanges.length != 1) {
            rangeIndex = threadLocalRandom.nextInt(0, valueRanges.length);
        }
        ValueRange valueRange = valueRanges[rangeIndex];

        return next(valueRange.min,valueRange.max);
    }

    private double next(int min,int max) {
        int value;
        if (min == max) {
            value = max;
        } else {
            value = threadLocalRandom.nextInt(0, max - min) + min;
        }
        return new BigDecimal(value)
                .divide(BigDecimal.valueOf(power10(fraction)), fraction, RoundingMode.UNNECESSARY)
                .doubleValue()
                ;
    }

    @AllArgsConstructor
    private static class ValueRange {
        final int min;//inclusive
        final int max;//exclusive
    }

    public static class Builder {
        //= , !=  这两个都会拆分成两个!(=,=)和(-无穷,A)(A,+无穷) ; [>,<] , [>=,<=] 不会超过2个逻辑运算符
        private final LogicalType[] logicalTypes = new LogicalType[2];
        private final double[] thresholdNums = new double[2];
        //private int operatorNum = 0;
        private final List<ValueRange> valueRanges = new ArrayList<>();
        private int index = -1;
        private int fraction = 0;//保留最大的小数位
        // MIN,MAX?
        private double maxDeviate = 100; //最大偏差值,当出现无穷范围的之后用于限制生成数的最大偏差范围
        private boolean init = false;
        private boolean positive = false;//negative

        Builder() {
        }

        public NumberGenerator build() {
            if (init) {
                throw new IllegalStateException("Builder has been initialized before");
            }
            //统一改成 [A,B)
            preProcessWithParams();
            init = true;
            return new NumberGenerator(this);
        }

        public Builder min(double min, boolean minInclusive) {
            if (minInclusive) {
                greaterEquals(min);
            } else {
                greaterThan(min);
            }
            return this;
        }

        public Builder max(double max, boolean maxInclusive) {
            if (maxInclusive) {
                lessEquals(max);
            } else {
                lessThan(max);
            }
            return this;
        }

        public Builder range(double min, boolean minInclusive, double max, boolean maxInclusive) {
            assert min < max;
            min(min, minInclusive);
            //---------------------------
            max(max, maxInclusive);
            return this;
        }


        public Builder positive() {
            this.positive = true;
            return this;
        }

        public Builder fraction(int fraction) {
            this.fraction = fraction;
            return this;
        }

        public Builder maxDeviate(double maxDeviate) {
            this.maxDeviate = maxDeviate;
            return this;
        }

        private void next(LogicalType logicalType, double number) {
//            if (operatorNum == 2) {
//                throw new IllegalStateException("添加的操作符号不能大于2个");
//            }
            boolean reset = index == -1;
            int pos = ++index % 2;
            logicalTypes[pos] = logicalType;
            thresholdNums[pos] = number;
            if (reset) {
                logicalTypes[pos + 1] = null;
                thresholdNums[pos + 1] = 0;
                fraction = 0;
            }
        }
//        public Builder reset(){}

        public Builder lessThan(double number) {
            next(LogicalType.LOGIC_LT, number);
            return this;
        }

        public Builder lessEquals(double number) {
            next(LogicalType.LOGIC_LE, number);
            return this;
        }

        public Builder greaterThan(double number) {
            next(LogicalType.LOGIC_GT, number);
            return this;
        }

        public Builder greaterEquals(double number) {
            next(LogicalType.LOGIC_GE, number);
            return this;
        }

        public Builder add(LogicalType logicalType, double number) {
            next(logicalType, number);
            return this;
        }

        public Builder equals(double number) {
            //optimize 可以不用膨胀和转换
            logicalTypes[0] = LogicalType.LOGIC_EQ0;
            thresholdNums[0] = number;

            logicalTypes[1] = LogicalType.LOGIC_EQ0;
            thresholdNums[1] = number;

            fraction = NumberUtils.fraction(number);
            index = -1;
            return this;
        }

        // (-无穷,A) , (A,+无穷)
        public Builder notEquals(double number) {
            logicalTypes[0] = LogicalType.LOGIC_GT;
            thresholdNums[0] = number;

            logicalTypes[1] = LogicalType.LOGIC_LT;
            thresholdNums[1] = number;

            //根据小数点直接得出fraction值
            fraction = NumberUtils.fraction(number);
            index = -1;
            return this;
        }

        private void preProcessWithParams() {
            // TODO 2.
            //1.做两个logicType是否存在重合/覆盖的情况, 甚至出现 '='和其他逻辑运算符同时出现的情况
            //2.如果是 eq的情况 如何优化？

            LogicalType logicalTypeA = logicalTypes[0];
            double thresholdNumA = thresholdNums[0];
            //将小数都转为整数
            int inflateNumA = inflateAndConvert(thresholdNumA);

            LogicalType logicalTypeB = logicalTypes[1];
            double thresholdNumB = thresholdNums[1];
            int inflateNumB = inflateAndConvert(thresholdNumB);

            //1.如果是单个区间范围内的
            if (logicalTypeB != null) {
                if (thresholdNumA < thresholdNumB) {
                    if (isGtType(logicalTypeA) && isLtType(logicalTypeB)) {
                        buildRange(inflateNumA, inflateNumB, logicalTypeA, logicalTypeB);
                        return;
                    }
                } else if (thresholdNumA > thresholdNumB) {
                    if (isGtType(logicalTypeB) && isLtType(logicalTypeA)) {
                        buildRange(inflateNumB, inflateNumA, logicalTypeB, logicalTypeA);
                        return;
                    }
                }
            }
            //2.两个不相交的区间内的
            int inflateMaxDeviate = BigDecimal.valueOf(maxDeviate)
                          .multiply(BigDecimal.valueOf(power10(fraction)))
                             .intValue();//what if 溢出?

            buildRange(inflateNumA, logicalTypeA, inflateMaxDeviate);

            if (logicalTypeB != null) {
                buildRange(inflateNumB, logicalTypeB, inflateMaxDeviate);
            }
        }

        public void buildRange(int min, int max, LogicalType minLogic, LogicalType maxLogic) {

            int minIncludeValue = 0, maxExcludeValue = 0;

            if (minLogic == LogicalType.LOGIC_GT) {
                minIncludeValue = min + 1;
            } else if (minLogic == LogicalType.LOGIC_GE) {
                minIncludeValue = min;
            }

            if (maxLogic == LogicalType.LOGIC_LT) {
                maxExcludeValue = max;
            } else if (maxLogic == LogicalType.LOGIC_LE) {
                maxExcludeValue = max + 1;
            }

            addValueRange(minIncludeValue, maxExcludeValue);
        }

        private void buildRange(int thresholdNumInt, LogicalType logicalType, int inflateMaxDeviate) {

            int minIncludeValue = 0, maxExcludeValue = 0;

            if (logicalType == LogicalType.LOGIC_LT) {
                minIncludeValue = thresholdNumInt - inflateMaxDeviate;
                maxExcludeValue = thresholdNumInt;

            } else if (logicalType == LogicalType.LOGIC_LE) {
                minIncludeValue = thresholdNumInt - inflateMaxDeviate + 1;
                maxExcludeValue = thresholdNumInt + 1;

            } else if (logicalType == LogicalType.LOGIC_GT) {
                minIncludeValue = thresholdNumInt + 1;
                maxExcludeValue = thresholdNumInt + inflateMaxDeviate + 1;

            } else if (logicalType == LogicalType.LOGIC_GE) {
                minIncludeValue = thresholdNumInt;
                maxExcludeValue = thresholdNumInt + inflateMaxDeviate;

            } else if (logicalType == LogicalType.LOGIC_EQ0) {
                minIncludeValue = thresholdNumInt;
                maxExcludeValue = thresholdNumInt + 1;

            }


            addValueRange(minIncludeValue, maxExcludeValue);
        }

        private void addValueRange(int minIncludeValue, int maxExcludeValue) {
            if (positive) {
                minIncludeValue = Math.max(0, minIncludeValue);
            }
            ValueRange valueRange = new ValueRange(minIncludeValue, maxExcludeValue);
            valueRanges.add(valueRange);
        }

        private boolean isLtType(LogicalType logicType) {
            return LogicalType.LOGIC_LT == logicType || LogicalType.LOGIC_LE == logicType;
        }

        private boolean isGtType(LogicalType logicType) {
            return LogicalType.LOGIC_GT == logicType || LogicalType.LOGIC_GE == logicType;
        }

        private int inflateAndConvert(double thresholdNum) {
            double inflateThresholdNum = thresholdNum * power10(fraction);
            if (inflateThresholdNum > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("阈值" + thresholdNum + "膨胀后的值大于Integer.MAX_VALUE");
            }
            return (int) inflateThresholdNum;
        }

    }
}
