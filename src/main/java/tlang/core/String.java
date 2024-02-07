package tlang.core;

public class String {

    private final java.lang.String value;

    public String(java.lang.String value) {
        this.value = value;
    }

    public java.lang.String get() {
        return value;
    }

    public java.lang.String toString() {
        return value;
    }

    public Bool isEqual(String obj) {
        return new Bool(value.equals(obj.get()));
    }
}
