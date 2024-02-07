package tlang;

import tlang.core.Bool;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.Value;

public interface Entity {
    Null<Value<?>> getAttr(String name);

    Bool hasAttrs();

    Bool exists(String name);
}
