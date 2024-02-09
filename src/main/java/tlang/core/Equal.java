package tlang.core;

import tlang.core.func.FuncRet;

public class Equal {

    public static FuncRet<Bool> equals(Value<?> value1, Value<?> value2) {
        return FuncRet.of(new Bool(value1.value().equals(value2.value())));
    }
}
