package tlang.core.func;

import tlang.core.Value;

public interface ApplyFunc<T, U> {

    FuncRet<T> apply(Value<U> value);
}
