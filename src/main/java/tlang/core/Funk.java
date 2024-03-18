package tlang.core;

import tlang.core.func.Func;
import tlang.core.func.FuncDef;
import tlang.core.func.FuncRet;
import tlang.internal.ClassType;

public class Funk implements Entity {

    private Value value;

    public static FuncRet from(Funk func, Value value) {
        func.value = value;
        return FuncRet.of(func);
    }

    public static FuncRet ifElse(Funk funk, FuncDef cond, FuncDef ifTrue, FuncDef ifFalse) {
        var ret = ((Bool) Func.from(cond, funk.value).apply().get()).get() ? Func.from(ifTrue, funk.value).apply() : Func.from(ifFalse, funk.value).apply();
        if (ret.isSuccess()) {
            funk.value = ret.getValue();
            return FuncRet.of(funk);
        } else {
            return ret;
        }
    }

    public static FuncRet ifTrue(Funk funk, FuncDef cond, FuncDef ifTrue) {
        if (((Bool) Func.from(cond, funk.value).apply().get()).get()) {
            var ret = Func.from(ifTrue, funk.value).apply();
            if (ret.isSuccess()) {
                funk.value = ret.getValue();
            } else {
                return ret;
            }
        }
        return FuncRet.of(funk);
    }

    public static FuncRet ifFalse(Funk funk, FuncDef cond, FuncDef ifFalse) {
        if (!((Bool) Func.from(cond, funk.value).apply().get()).get()) {
            var ret = Func.from(ifFalse, funk.value).apply();
            if (ret.isSuccess()) {
                funk.value = ret.getValue();
            } else {
                return ret;
            }
        }
        return FuncRet.of(funk);
    }

    public static FuncRet foreach(Funk funk, FuncDef func) {
        for (var value : ((Array) funk.value).getRecords()) {
            var ret = Func.from(func, value).apply();
            if (ret.isError()) {
                return ret;
            }
        }
        return FuncRet.of(funk);
    }

    @Override
    public Value getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(this.getClass());
    }

    @Override
    public Null getModel() {
        return Null.empty();
    }
}
