package tlang.core.func;

import tlang.core.Value;
import tlang.core.Void;

public interface OnResult<T extends Value<T>> {

    FuncRet<Void> onResult(T args);
}
