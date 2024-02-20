package tlang.core;

import tlang.internal.ClassType;
import tlang.internal.ContextContent;

public class Double implements Value<Double> {

    public static final Type TYPE = ClassType.of(Double.class);

    private final double value;

    public Double(double value) {
        this.value = value;
    }

    public double get() {
        return value;
    }

    @Override
    public Null<ContextContent> getContext() {
        return null;
    }

    @Override
    public Double getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
