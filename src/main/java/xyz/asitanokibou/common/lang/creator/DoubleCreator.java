package xyz.asitanokibou.common.lang.creator;

import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
//溢出 由使用这自行判断和处理
public class DoubleCreator extends AbstractDataPointCreator<Double>{
    
    public DoubleCreator(Double start, Double end, double offset) {
        this(start, end, offset,false,false);
    }

    @AllArgsConstructor
    private static class DoubleNumberStep implements Step<Double> {
        public static DoubleNumberStep of(double offset) {
            return new DoubleNumberStep(BigDecimal.valueOf(offset));
        }
        private final BigDecimal offset;
        @Override
        public Double forward(@Nonnull Double current) {
            return BigDecimal.valueOf(current).add(offset).doubleValue();
        }
        @Override
        public Double backward(@Nonnull Double current) {
            return BigDecimal.valueOf(current).subtract(offset).doubleValue();
        }
    }

    public DoubleCreator(Double start, Double end, double offset, boolean cycle) {
        super(start, end, DoubleNumberStep.of(offset), false, cycle);
    }

    public DoubleCreator(Double start, Double end, double offset, boolean reverse, boolean cycle) {
        super(start, end, DoubleNumberStep.of(offset), reverse, cycle);
    }

}
