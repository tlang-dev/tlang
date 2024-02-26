package tlang.mutable;

import tlang.core.Type;
import tlang.core.Value;
import tlang.internal.ClassType;

public class Map implements Value {
    @Override
    public Value getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(this.getClass());
    }
}
