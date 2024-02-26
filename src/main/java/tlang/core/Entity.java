package tlang.core;

import tlang.core.func.FuncRet;

public interface Entity extends Value {
    Null getAttr(String name);

    Bool hasAttrs();

    Bool exists(String name);

    Null getAttr(Int index);

    Bool exists(Int index);

    FuncRet call(String name, Array args);

    FuncRet call(Int index, Array args);

    Type getType();

    Null getModel();
}
