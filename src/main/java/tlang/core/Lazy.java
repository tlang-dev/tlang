package tlang.core;

import tlang.internal.ClassType;

public class Lazy implements Value {

    private Value value;

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return ClassType.of(Lazy.class);
    }

    public void setValue(Value value) {
        this.value = value;
    }

}
