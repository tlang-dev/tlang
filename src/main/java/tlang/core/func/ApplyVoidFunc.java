package tlang.core.func;

import tlang.core.Error;
import tlang.core.Value;
import tlang.core.Void;

public interface ApplyVoidFunc<T extends Value<T>> {

    FuncRet<Void> apply(T value);
}
