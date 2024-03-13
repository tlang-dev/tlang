package tlang.core;

import tlang.core.func.FuncRet;
import tlang.internal.ClassType;

public interface Entity extends Value {

    public static final Type TYPE = ClassType.of(Entity.class);

    Type getType();

    Null getModel();
}
