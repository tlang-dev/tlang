package tlang.core;

public class Set<T> implements Value<Set<T>> {
    private final String key;
    private final T value;

    public Set(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    @Override
    public Set<T> getElement() {
        return this;
    }

    @Override
    public String getType() {
        return null;
    }
}
