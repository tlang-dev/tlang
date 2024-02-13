package tlang.core;

public interface Model extends Value<Model> {

    <T extends Value<T>> Null<T> getAttr(String name);

    Bool hasAttrs();

    Bool attrExists(String name);

    <T extends Value<T>> Null<T> getParam(String name);

    Bool hasParams();

    Bool ParamExists(String name);

    Type getType();

    Null<Model> getModel();
}
