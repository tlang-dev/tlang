package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.internal.ClassType;

public class Bool implements Value {

    public static final Type TYPE = ClassType.of(Bool.class);

    public static final Bool TRUE = new Bool(true);

    public static final Bool FALSE = new Bool(false);

    private final boolean value;

    public Bool(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public FuncRet ifTrue(ApplyVoidFunc func) {
        return Bool.ifTrue(this, func);
    }

    public FuncRet ifFalse(ApplyVoidFunc func) {
        return Bool.ifFalse(this, func);
    }

    public static FuncRet isEqual(Bool that, Bool other) {
        return FuncRet.of(new Bool(that.get() == other.get()));
    }

    public static FuncRet isNotEqual(Bool that, Bool other) {
        return FuncRet.of(new Bool(that.get() != other.get()));
    }

    public static FuncRet ifTrue(Bool that, ApplyVoidFunc func) {
        if (that.get()) {
            func.apply(Void.VOID);
        }
        return FuncRet.VOID;
    }

    public static FuncRet ifFalse(Bool that, ApplyVoidFunc func) {
        if (!that.get()) {
            func.apply(Void.VOID);
        }
        return FuncRet.VOID;
    }

    public static FuncRet and(Bool that, Bool other) {
        return FuncRet.of(new Bool(that.get() && other.get()));
    }

    public static FuncRet or(Bool that, Bool other) {
        return FuncRet.of(new Bool(that.get() || other.get()));
    }

    public static FuncRet fold(Bool that, ApplyVoidFunc ifTrue, ApplyVoidFunc ifFalse) {
        if (that.get()) {
            ifTrue.apply(Void.VOID);
        } else {
            ifFalse.apply(Void.VOID);
        }
        return FuncRet.VOID;
    }

    @Override
    public Bool getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

}
