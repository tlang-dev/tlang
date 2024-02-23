package tlang.core;

public class None<T> implements Value<T> {

    @Override
    public T getElement() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
