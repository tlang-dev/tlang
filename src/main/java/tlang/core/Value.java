package tlang.core;

import tlang.internal.Element;

public interface Value<T> extends Element<T> {

    default Int compareTo(Value<T> other) {
        return new Int(0);
    }
}
