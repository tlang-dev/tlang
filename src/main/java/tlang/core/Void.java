package tlang.core;

import tlang.internal.ClassType;
import tlang.internal.ContextContent;

public class Void implements Value {

    public static final Type TYPE = ClassType.of(Void.class);


    public static final Void VOID = new Void();

    private Void() {
    }

    @Override
    public Void getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

}
