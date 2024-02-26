package tlang.core;

import tlang.core.func.Apply;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.internal.ClassType;

public class Null implements Value {

    public static final Type TYPE = ClassType.of(Null.class);

    private final Value value;

    public Null() {
        this.value = new None();
    }

    public Null(Value value) {
        this.value = value;
    }

    public Value get() {
        return value;
    }

    public Bool isNull() {
        return new Bool(value == null);
    }

    public Bool isNotNull() {
        return new Bool(value != null);
    }

    public Value map(MapFunc func) {
        return func.apply(value);
    }

    public FuncRet orElse(Value orElse) {
        return new FuncRet((this.get() == null ? orElse : this.get()));
    }

    public Null ifNull(Apply func) {
        if (value == null) {
            func.apply();
        }
        return this;
    }

    public Null ifNotNull(ApplyVoidFunc func) {
        if (value != null) {
            func.apply(value.getValue());
        }
        return this;
    }

    public static Null of(Value value) {
        return new Null(value);
    }

    public static Null empty() {
        return new Null();
    }

    public static FuncRet orElse(Null that, Value orElse) {
        return new FuncRet((that.get() == null ? orElse : that.get()));
    }

    @Override
    public Null getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

}
