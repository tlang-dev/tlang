package tlang.core;

import tlang.internal.ClassType;
import tlang.internal.ContextContent;

public class Int implements Value<Int> {

    public static final Type TYPE = ClassType.of(Int.class);

    private final int value;

    public Int(int value) {
        this.value = value;
    }

    public int get() {
        return value;
    }

    @Override
    public Int getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
