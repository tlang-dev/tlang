package tlang.core;

import tlang.core.func.Apply;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.internal.ContextContent;

public class Null<T extends Value<T>> implements Value<Null<T>> {

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

    public FuncRet<T> orElse(T orElse) {
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
            func.apply(value);
        }
        return this;
    }

    public static <T extends Value<T>> Null<T> of(T value) {
        return new Null<>(value);
    }

    public static <T extends Value<T>> Null<T> empty() {
        return new Null<>();
    }

    public static <T extends Value<T>> FuncRet<T> orElse(Null<T> that, T orElse) {
        return new FuncRet<>((that.get() == null ? orElse : that.get()));
    }

    @Override
    public Null<T> getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Null<ContextContent> getContext() {
        return Null.empty();
    }
}
