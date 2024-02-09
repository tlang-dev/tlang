package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.mutable.List;

public class Match {

    public static <T> FuncRet<Null<Value<?>>> match(Value<T> value, Case<T>[] cases) {
        for (Case<T> c : cases) {
            if (Case.isApplicable(c, value).get().get().value().get()) {
                return FuncRet.ofNull(c.func().apply(c.value()));
            }
        }
        return FuncRet.ofNull();
    }

    public static <T> FuncRet<Null<Value<?>>> match(Value<T> value, Case<T>[] cases, ApplyVoidFunc<Value<T>> defaultCase) {
        for (Case<T> c : cases) {
            if (Case.isApplicable(c, value).get().get().value().get()) {
                return FuncRet.ofNull(c.func().apply(c.value()));
            }
        }
        defaultCase.apply(value);
        return FuncRet.ofNull();
    }

    public static <T, U> FuncRet<Array<Value<U>>> matchAny(Value<T> value, Case<U>[] cases) {
        var result = new List<Value<U>>();
        for (Case<U> c : cases) {
            if (Case.isApplicable(c, value).get().get().value().get()) {
                result.add(c.func().apply(value));
            }
        }
        return result.toArray();
    }

}
