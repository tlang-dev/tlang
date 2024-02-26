package tlang.core;

import tlang.internal.ClassType;

public class Int implements Value {

    public static final Type TYPE = ClassType.of(Int.class);

    private final int value;

    public Int(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    @Override
    public Int getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
