package tlang.core;

public class Bool {

    public static final Bool TRUE = new Bool(true);

    public static final Bool FALSE = new Bool(false);

    private final boolean value;

    public Bool(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }
}
