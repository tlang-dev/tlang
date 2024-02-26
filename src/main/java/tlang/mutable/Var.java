package tlang.mutable;

import tlang.core.Type;
import tlang.core.Value;
import tlang.internal.ClassType;

public class Var implements Value {

    private Value value;

    public Var(Value value) {
        this.value = value;
    }

    public Value get() {
        return value;
    }

    public void set(Value value) {
        this.value = value;
    }

    @Override
    public Value getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(this.getClass());
    }
}
