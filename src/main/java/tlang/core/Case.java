package tlang.core;

import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;

public class Case<T> {

//    private final Value<?> value;
//    private final MapFunc<Value<?>> mapFunc;
//    private final Value<T> func;
//
//    public static <T> FuncRet<Bool> isApplicable(Case<T> that, Value<?> value) {
//        return Equal.equals(value, that.value());
//    }
//
//    public static <T> FuncRet<Null<Value<T>>> apply(Case<T> that, Value<?> value) {
//        if (Case.isApplicable(that, value).get().get().value().get()) {
//            return FuncRet.of(Null.of(that.func.apply(value)));
//        }
//        return FuncRet.ofNull();
//    }
}