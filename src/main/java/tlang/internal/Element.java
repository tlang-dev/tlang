package tlang.internal;

import tlang.core.Type;

public interface Element<T> {

    T getElement();

    Type getType();
}
