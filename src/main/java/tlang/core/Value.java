package tlang.core;

import tlang.core.func.FuncRet;

public interface Value {

    default Int compareTo(Value other) {
        return new Int(0);
    }

    Value getValue();

    Type getType();

    default Null getAttr(String name) {
        return Null.empty();
    }

    default Null getAttr(Int index) {
        return Null.empty();
    }

    default FuncRet callFunc(String name, Array args) {
        return FuncRet.VOID;
    }
}
