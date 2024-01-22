package tlang.mutable;

public class Var<T> {

    private T value;

    public Var(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
