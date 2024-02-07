package tlang.core;

import tlang.core.func.ApplyFunc;

public interface ImplicitMatch<T, U, V> {

    void match(ApplyFunc<T> first, Null<ApplyFunc<U>> second, Null<ApplyFunc<V>> last);
}
