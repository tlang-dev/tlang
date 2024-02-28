package tlang.internal;

import tlang.core.Type;

public interface Element<T> extends Context {

    T getElement();

    Type getType();
}
