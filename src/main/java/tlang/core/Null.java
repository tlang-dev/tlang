package tlang.core;

import tlang.core.func.Apply;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.internal.ClassType;
import tlang.internal.ContextContent;

public class Null<T> implements Value<Null<T>> {

    public static final Type TYPE = ClassType.of(Null.class);

    private final Value<T> value;

    public Null() {
        this.value = new None<>();
    }

    public Null(Value<T> value) {
        this.value = value;
    }

    public Value<T> get() {
        return value;
    }

    public Bool isNull() {
        return new Bool(value == null);
    }

    public Bool isNotNull() {
        return new Bool(value != null);
    }

    public <B> Value<B> map(MapFunc<T, B> func) {
        return func.apply(value);
    }

    public FuncRet<T> orElse(Value<T> orElse) {
        return new FuncRet<T>((this.get() == null ? orElse : this.get()));
    }

    public Null<T> ifNull(Apply func) {
        if (value == null) {
            func.apply();
        }
        return this;
    }

    public Null<T> ifNotNull(ApplyVoidFunc<T> func) {
        if (value != null) {
            func.apply(value.getElement());
        }
        return this;
    }

    public static <T> Null<T> of(Value<T> value) {
        return new Null<>(value);
    }

    public static <T> Null<T> empty() {
        return new Null<>();
    }

    public static <T> FuncRet<T> orElse(Null<T> that, Value<T> orElse) {
        return new FuncRet<>((that.get() == null ? orElse : that.get()));
    }

    @Override
    public Null<T> getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

}
