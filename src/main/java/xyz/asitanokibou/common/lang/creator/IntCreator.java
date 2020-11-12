package xyz.asitanokibou.common.lang.creator;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.Objects;


@Deprecated
public class IntCreator implements ValueCreator<Integer>{

    private final Integer start;
    private final Integer end;
    private final int offset;
//    private final boolean reverse;

    public IntCreator(Integer start, Integer end,int offset) {
        this(start, end, offset,false);
    }

    public IntCreator(Integer start, Integer end,int offset,boolean reverse) {
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
        this.offset = offset;
//        this.reverse = reverse;
    }

    class IntIterator implements Iterator<Integer> {
        Integer last = null;
        @Override
        public boolean hasNext() {
            return last == null || (last + offset) < end;
        }

        @Override
        public Integer next() {
            last = last == null ? start : last + offset;
            return null;
        }
    }

    @Nonnull
    @Override
    public Iterator<Integer> iterator() {
        return new IntIterator();
    }
}
