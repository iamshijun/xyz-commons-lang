package xyz.asitanokibou.common.lang.creator;

import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;


public class IntegerCreator extends AbstractDataPointCreator<Integer> {

    public IntegerCreator(Integer start, Integer end) {
        this(start, end, 1);
    }

    public IntegerCreator(Integer start, Integer end, int offset) {
        this(start, end, offset, false, false);
    }

    @AllArgsConstructor
    private static class IntNumberStep implements Step<Integer> {
        private int offset;

        //TODO 溢出处理
        @Override
        public Integer forward(@Nonnull Integer current) {
            return current + offset;
        }

        @Override
        public Integer backward(@Nonnull Integer current) {
            return current - offset;
        }
    }

    public IntegerCreator(Integer start, Integer end, int offset, boolean cycle) {
        super(start, end, new IntNumberStep(offset), false, cycle);
    }

    public IntegerCreator(Integer start, Integer end, int offset, boolean reverse, boolean cycle) {
        super(start, end, new IntNumberStep(offset), reverse, cycle);
    }

}
