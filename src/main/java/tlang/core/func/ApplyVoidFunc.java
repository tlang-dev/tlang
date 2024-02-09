package tlang.core.func;

import tlang.core.Void;

public interface ApplyVoidFunc<T> {

    FuncRet<Void> apply(T value);
}
