package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;

public class Bool implements Value<Bool> {

    public static final Bool TRUE = new Bool(true);

    public static final Bool FALSE = new Bool(false);

    private final boolean value;

    public Bool(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    @Override
    public Bool value() {
        return this;
    }

    public static FuncRet<Bool> isEqual(Bool that, Bool other) {
        return FuncRet.of(new Bool(that.get() == other.get()));
    }

    public static FuncRet<Bool> isNotEqual(Bool that, Bool other) {
        return FuncRet.of(new Bool(that.get() != other.get()));
    }

    public static FuncRet<Void> ifTrue(Bool that, ApplyVoidFunc<Void> func) {
        if (that.get()) {
            func.apply(Void.VOID);
        }
        return FuncRet.VOID;
    }

    public static FuncRet<Void> ifFalse(Bool that, ApplyVoidFunc<Void> func) {
        if (!that.get()) {
            func.apply(Void.VOID);
        }
        return FuncRet.VOID;
    }

    public static FuncRet<Bool> and(Bool that, Bool other) {
        return FuncRet.of(new Bool(that.get() && other.get()));
    }

    public static FuncRet<Bool> or(Bool that, Bool other) {
        return FuncRet.of(new Bool(that.get() || other.get()));
    }

    public static FuncRet<Void> fold(Bool that, ApplyVoidFunc<Void> ifTrue, ApplyVoidFunc<Void> ifFalse) {
        if (that.get()) {
            ifTrue.apply(Void.VOID);
        } else {
            ifFalse.apply(Void.VOID);
        }
        return FuncRet.VOID;
    }
}
