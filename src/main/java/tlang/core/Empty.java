package tlang.core;

import tlang.internal.ContextContent;

public class Empty implements Value {

    @Override
    public Empty getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return null;
    }
}
