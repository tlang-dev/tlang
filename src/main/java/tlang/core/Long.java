package tlang.core;

import tlang.internal.ClassType;

public class Long implements Value {

    public static final Type TYPE = ClassType.of(Long.class);

    private final long value;

    public Long(long value) {
        this.value = value;
    }

    public Long(int value) {
        this.value = value;
    }

    public long get() {
        return value;
    }

    public int intValue() {
        return (int) value;
    }

    public String toStringValue() {
        return new String(java.lang.String.valueOf(value));
    }

    @Override
    public Long getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }
}
