package tlang.core.func;

import tlang.core.Error;
import tlang.core.Void;
import tlang.core.*;

import java.lang.String;

public class FuncRet<T extends Value<T>> implements Value<FuncRet<T>> {

    public static final FuncRet<Void> VOID = new FuncRet<>();

    private final Null<T> ret;

    private final Null<Error> error;

    public FuncRet() {
        this.ret = Null.empty();
        this.error = Null.empty();
    }

    public FuncRet(T ret) {
        this.ret = Null.of(ret);
        this.error = Null.empty();
    }

    public FuncRet(Error error) {
        this.ret = Null.empty();
        this.error = Null.of(error);
    }

    public Null<T> get() {
        return ret;
    }

    public FuncRet<T> onResult(OnResult<T> onResult) {
        ret.ifNotNull(onResult::onResult);
        return this;
    }

    public FuncRet<T> onError(OnError onError) {
        error.ifNotNull(onError::onError);
        return this;
    }

    public FuncRet<Bool> isError() {
        return FuncRet.of(error.isNull().get() ? Bool.FALSE : Bool.TRUE);
    }

    public FuncRet<Void> inAllCase(ApplyVoidFunc<T> func) {
        func.apply(ret.get());
        return FuncRet.VOID;
    }

    public FuncRet<Bool> isVoid() {
        return FuncRet.of(new Bool(ret.isNull().get() && error.isNull().get()));
    }

    public static <T extends Value<T>> FuncRet<T> of(Value<T> args) {
        return new FuncRet<>(args);
    }

    public static FuncRet<MultiValue> of(Value<?>... args) {
        return new FuncRet<>(new MultiValue(args));
    }

    public static FuncRet<Error> error(Error error) {
        return new FuncRet<>(error);
    }

    public static FuncRet<Error> error(String code, String message) {
        return new FuncRet<>(new Error(new tlang.core.String(code), Null.of(new tlang.core.String(message))));
    }

    public static FuncRet<Error> error(String code) {
        return new FuncRet<>(new Error(new tlang.core.String(code), Null.empty()));
    }

    public static <T extends Throwable> FuncRet<Error> error(T e) {
        return new FuncRet<>(new Error(new tlang.core.String(e.getClass().getSimpleName()), Null.of(new tlang.core.String(e.getMessage()))));
    }

    public static <T extends Value<T>> FuncRet<Null<T>> ofNull() {
        return new FuncRet<>(Null.empty());
    }

    public static <T extends Value<T>> FuncRet<Null<T>> ofNull(T value) {
        return new FuncRet<>(Null.of(value));
    }

    @Override
    public FuncRet<T> getElement() {
        return this;
    }

    @Override
    public tlang.core.String getType() {
        return null;
    }


}