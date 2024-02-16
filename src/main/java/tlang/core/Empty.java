package tlang.core;

import tlang.internal.ContextContent;

public class Empty implements Value<Empty> {

    @Override
    public Null<ContextContent> getContext() {
        return null;
    }

    @Override
    public Empty getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return null;
    }
}
