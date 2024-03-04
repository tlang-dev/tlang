package tlang.core;

import tlang.core.func.FuncRet;

public interface Value {

    default Int compareTo(Value other) {
        return new Int(0);
    }

    Value getValue();

    Type getType();

    default Value getAttr(String name) {
        return Null.empty();
    }

    default Value getAttr(Int index) {
        return Null.empty();
    }

    default FuncRet callFunc(String name, Array args) {
        return FuncRet.VOID;
    }
}
