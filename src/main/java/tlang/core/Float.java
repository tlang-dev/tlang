package tlang.core;

import tlang.internal.ClassType;

public class Float {

    public static final Type TYPE = ClassType.of(Float.class);

    private final float value;

    public Float(float value) {
        this.value = value;
    }

    public float get() {
        return value;
    }
}
