package tlang.core.func;

import tlang.core.Value;

public interface MapFunc<T, B> {

    Value<B> apply(Value<T> value);
}
