package tlang.core.func;

import tlang.core.Value;

public interface OnResult<T> {

    void onResult(Value<T> args);
}
