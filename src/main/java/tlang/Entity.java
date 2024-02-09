package tlang;

import tlang.core.String;
import tlang.core.*;

public interface Entity {
    Null<Value<?>> getAttr(String name);

    Bool hasAttrs();

    Bool exists(String name);

    Type getType();

    Null<Model> getModel();
}
