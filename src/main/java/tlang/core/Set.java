package tlang.core;

import tlang.internal.ClassType;

public class Set implements Value {
    private final String key;
    private final Value value;

    public Set(String key, Value value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }

    public Set getSet() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(Set.class);
    }

}
