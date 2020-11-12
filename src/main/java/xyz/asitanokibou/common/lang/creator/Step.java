package xyz.asitanokibou.common.lang.creator;

import javax.annotation.Nonnull;

/**
 * ValueCreator的步进接口
 *
 * @author aimysaber@gmail.com
 */
public interface Step<T> {

    /**
     * 往前移动 前进
     *
     * @param current 当前点
     * @return 下一个点
     */
    T forward(@Nonnull T current);

    /**
     * 往回移动 后退
     *
     * @param current 当前点
     * @return 前一个点
     */
    T backward(@Nonnull T current);
}
