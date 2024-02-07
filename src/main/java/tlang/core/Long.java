package tlang.core;

public class Long {

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
}
