package tlang.core.func;

import tlang.core.Long;

public interface MapFuncWithIndex<T, B> {

    B apply(T value, Long index);
}
