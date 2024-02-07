package tlang.core.func;

import tlang.core.Error;
import tlang.core.Void;
import tlang.core.*;

import java.lang.String;

public class FuncRet<T> implements Value<FuncRet<T>>, ImplicitMatch<Value<?>, Error, Void> {

    public static final FuncRet<Void> VOID = new FuncRet<>();

    private final Null<Value<T>> ret;

    private final Null<Error> error;

    public FuncRet() {
        this.ret = Null.empty();
        this.error = Null.empty();
    }

    public FuncRet(Value<T> ret) {
        this.ret = Null.of(ret);
        this.error = Null.empty();
    }

    public FuncRet(Error error) {
        this.ret = Null.empty();
        this.error = Null.of(error);
    }

    public Null<Value<T>> get() {
        return ret;
    }

    public FuncRet<T> onResult(OnResult<T> onResult) {
        if (!error.isNull()) {
            onResult.onResult(ret.get());
        }
        return this;
    }

    public FuncRet<T> onError(OnError onError) {
        error.ifNotNull(onError::onError);
        return this;
    }

    public Bool isError() {
        return error.isNull() ? Bool.FALSE : Bool.TRUE;
    }

    public FuncRet<Void> inAllCase(ApplyFunc<Value<T>> func) {
        func.apply(ret.get());
        return FuncRet.VOID;
    }

    public boolean isVoid() {
        return ret.isNull() && error.isNull();
    }

    public static <T> FuncRet<T> of(Value<T> args) {
        return new FuncRet<>(args);
    }

    public static FuncRet<MultiValue> of(Value<?>... args) {
        return new FuncRet<>(new MultiValue(args));
    }

    public static FuncRet<Error> error(Error error) {
        return new FuncRet<>(error);
    }

    public static FuncRet<Error> error(String code, String message) {
        return new FuncRet<Error>(new Error(new tlang.core.String(code), Null.of(new tlang.core.String(message))));
    }

    public static FuncRet<Error> error(String code) {
        return new FuncRet<Error>(new Error(new tlang.core.String(code), Null.empty()));
    }

    public static <T extends Throwable> FuncRet<Error> error(T e) {
        return new FuncRet<>(new Error(new tlang.core.String(e.getClass().getSimpleName()), Null.of(new tlang.core.String(e.getMessage()))));
    }

    @Override
    public void match(ApplyFunc<Value<?>> first, Null<ApplyFunc<Error>> second, Null<ApplyFunc<Void>> last) {
        if (error.isNull()) {
            first.apply(ret.get());
        } else {
            second.ifNotNull(func -> func.apply(error.get()));
        }
        last.ifNotNull(func -> func.apply(tlang.core.Void.VOID));
    }
}
