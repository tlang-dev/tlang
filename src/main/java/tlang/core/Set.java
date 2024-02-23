package tlang.core;

import tlang.internal.ClassType;

public class Set<T> implements Value<Set<T>> {
    private final String key;
    private final Value<T> value;

    public Set(String key, Value<T> value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Value<T> getValue() {
        return value;
    }

    @Override
    public Set<T> getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(Set.class);
    }

}
