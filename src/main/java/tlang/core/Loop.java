package tlang.core;

import tlang.core.func.ApplyFunc;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.mutable.List;

public class Loop {

    /*public static <T> FuncRet<Void> forEach(Array<Value<T>> array, ApplyVoidFunc<Value<T>> func) {
        for (Value<T> value : array.getRecords()) {
            func.apply(value);
        }
        return FuncRet.VOID;
    }

    public static <T> FuncRet<Array<T>> filter(Array<Value<T>> array, ApplyFunc<Bool, T> func) {
        var result = new List<T>();
        for (Value<T> value : array.getRecords()) {
            if (func.apply(value).get().get().value().get()) {
                result.add(value.value());
            }
        }
        return result.toArray();
    }*/


}
