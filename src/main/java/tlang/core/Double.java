package tlang.core;

import tlang.internal.ClassType;

public class Double implements Value {

    public static final Type TYPE = ClassType.of(Double.class);

    private final double value;

    public Double(double value) {
        this.value = value;
    }

    public double get() {
        return value;
    }

    @Override
    public Double getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
