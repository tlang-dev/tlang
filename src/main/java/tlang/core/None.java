package tlang.core;

import tlang.internal.ContextContent;

public class None<T> implements Value<T> {
    @Override
    public Null<ContextContent> getContext() {
        return null;
    }

    @Override
    public T getElement() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
