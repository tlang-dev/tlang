package tlang.core;

import tlang.internal.ClassType;
import tlang.internal.ContextContent;

public class Void implements Value<Void> {

    public static final Type TYPE = ClassType.of(Void.class);


    public static final Void VOID = new Void();

    private Void() {
    }

    @Override
    public Void getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public Null<ContextContent> getContext() {
        return null;
    }

}
