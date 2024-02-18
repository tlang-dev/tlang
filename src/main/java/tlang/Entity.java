package tlang;

import tlang.core.String;
import tlang.core.*;

public interface Entity extends Value<Entity> {
    <T extends Value<T>> Null<T> getAttr(String name);

    Bool hasAttrs();

    Bool exists(String name);

    Type getType();

    Null<Model> getModel();
}
