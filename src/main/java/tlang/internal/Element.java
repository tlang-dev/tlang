package tlang.internal;

import tlang.core.Type;

public interface Element<T> extends AstContext {

    T getElement();

    Type getType();
}
