package tlang.core;

public class Type<T> {

    private final T value;

    public Type(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public static Type<Void> VOID = new Type<>(Void.VOID);
}
