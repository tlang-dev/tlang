package tlang.core;

public interface Model extends Value {

    Null getAttr(String name);

    Bool hasAttrs();

    Bool attrExists(String name);

    Null getParam(String name);

    Bool hasParams();

    Bool ParamExists(String name);

    Type getType();

    Null getModel();
}
