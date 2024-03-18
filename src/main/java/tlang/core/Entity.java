package tlang.core;

import tlang.internal.ClassType;

public interface Entity extends Value {

    Type TYPE = ClassType.of(Entity.class);

    Type getType();

    Null getModel();
}
