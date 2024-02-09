package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;

public interface ImplicitMatch<T, U, V> {

    FuncRet<Void> match(ApplyVoidFunc<T> first, Null<ApplyVoidFunc<U>> second, Null<ApplyVoidFunc<V>> last);
}
