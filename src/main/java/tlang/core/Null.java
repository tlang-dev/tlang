package tlang.core;

import tlang.core.func.Apply;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;

public class Null<T> implements Value<Null<T>> {

    private final T value;

    public Null() {
        this.value = null;
    }

    public Null(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public Bool isNull() {
        return new Bool(value == null);
    }

    public Bool isNotNull() {
        return new Bool(value != null);
    }

    public <B> B map(MapFunc<T, B> func) {
        return func.apply(value);
    }

    public Null<T> ifNull(Apply func) {
        if (value == null) {
            func.apply();
        }
        return this;
    }

    public Null<T> ifNotNull(ApplyVoidFunc<T> func) {
        if (value != null) {
            func.apply(value);
        }
        return this;
    }

    public static <T> Null<T> of(T value) {
        return new Null<>(value);
    }

    public static <T> Null<T> empty() {
        return new Null<>();
    }

    @Override
    public Null<T> value() {
        return this;
    }
}
