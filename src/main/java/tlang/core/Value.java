package tlang.core;

import tlang.internal.Element;

public interface Value<T> {

    default Int compareTo(Value<T> other) {
        return new Int(0);
    }

    T getElement();

    Type getType();
}
