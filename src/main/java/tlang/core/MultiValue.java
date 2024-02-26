package tlang.core;

import tlang.internal.ClassType;

public class MultiValue implements Value {

    private final Value[] values;

    public MultiValue(Value... values) {
        this.values = values;
    }

    public Value[] getValues() {
        return values;
    }

    @Override
    public MultiValue getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(MultiValue.class);
    }
}
