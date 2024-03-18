package tlang.core.func;

import tlang.core.Error;
import tlang.core.*;
import tlang.internal.ClassType;

import java.lang.String;

public class FuncRet implements Entity {

    public static final FuncRet VOID = new FuncRet();

    private final Null ret;

    private final Null error;

    public FuncRet() {
        this.ret = Null.empty();
        this.error = Null.empty();
    }

    public FuncRet(Value ret) {
        this.ret = Null.of(ret);
        this.error = Null.empty();
    }

    public FuncRet(Error error) {
        this.ret = Null.empty();
        this.error = Null.of(error);
    }

    public Value get() {
        return ret.get();
    }

    private Error getError() {
        return (Error) error.get();
    }

   /* public final FuncDef onResult = new FuncImpl(FuncRet.class,
            args -> {
                get().ifNotNull(value -> ((OnResult) args.get(new Int(0)).getValue()).call(new Array(new Value[]{value})));
                return FuncRet.VOID;
            }).getValue();

    public final FuncDef onError = new FuncImpl(FuncRet.class,
            args -> {
                getError().ifNotNull(value -> ((OnError) args.get(new Int(0)).getValue()).call(new Array(new Value[]{value})));
                return FuncRet.VOID;
            }).getValue();*/

    public boolean isError() {
        return (error.isNull().get() ? Bool.FALSE : Bool.TRUE).get();
    }

    public FuncRet inAllCase(ApplyVoidFunc func) {
        func.apply(ret.get());
        return FuncRet.VOID;
    }

    public boolean isVoid() {
        return ret.isNull().get() && error.isNull().get();
    }

    public boolean isSuccess() {
        return ret.isNotNull().get() && error.isNull().get();
    }

    public static FuncRet of(Value args) {
        return new FuncRet(args);
    }

    public static FuncRet of(Array args) {
        return new FuncRet(new MultiValue(args));
    }

    public static FuncRet error(Error error) {
        return new FuncRet(error);
    }

    public static FuncRet error(String code, String message) {
        return new FuncRet(new Error(new tlang.core.String(code), Null.of(new tlang.core.String(message))));
    }

    public static FuncRet error(String code) {
        return new FuncRet(new Error(new tlang.core.String(code), Null.empty()));
    }

    public static FuncRet error(Exception e) {
        return new FuncRet(new Error(new tlang.core.String(e.getClass().getSimpleName()), Null.of(new tlang.core.String(e.getMessage()))));
    }

    public static FuncRet ofNull() {
        return new FuncRet(Null.empty());
    }

    public static FuncRet ofNull(Value value) {
        return new FuncRet(Null.of(value));
    }

    @Override
    public FuncRet getValue() {
        return this;
    }

    @Override
    public Null getAttr(tlang.core.String name) {
        return null;
    }

/*    @Override
    public Bool hasAttrs() {
        return null;
    }

    @Override
    public Bool exists(tlang.core.String name) {
        return null;
    }*/

    @Override
    public Null getAttr(Int index) {
        return null;
    }

    /*@Override
    public Bool exists(Int index) {
        return null;
    }

    @Override
    public FuncRet call(tlang.core.String name, Array args) {
        return switch (name.get()) {
            case "onError" -> onError.call(args);
            case "onResult" -> onResult.call(args);
            default -> null;
        };
    }

    @Override
    public FuncRet call(Int index, Array args) {
        return switch (index.get()) {
            case 0 -> onResult.call(args);
            case 1 -> onError.call(args);
            default -> null;
        };
    }*/

    @Override
    public Type getType() {
        return ClassType.of(FuncRet.class);
    }

    @Override
    public Null getModel() {
        return Null.empty();
    }

}
