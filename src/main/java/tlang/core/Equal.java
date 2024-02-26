package tlang.core;

import tlang.core.func.FuncRet;

public class Equal {

    public static FuncRet equals(Value value1, Value value2) {
        return FuncRet.of(new Bool(value1.getValue().equals(value2.getValue())));
    }
}
