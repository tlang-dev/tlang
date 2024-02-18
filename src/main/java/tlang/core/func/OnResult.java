package tlang.core.func;

import tlang.core.Value;
import tlang.core.Void;

public interface OnResult<T> {

    FuncRet<Void> onResult(Value<T> args);
}
