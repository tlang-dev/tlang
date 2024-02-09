package tlang.core;

public interface Model {

    Null<Value<?>> getAttr(String name);

    Bool hasAttrs();

    Bool attrExists(String name);

    Null<Value<?>> getParam(String name);

    Bool hasParams();

    Bool ParamExists(String name);

    Type getType();

    Null<Model> getModel();
}
