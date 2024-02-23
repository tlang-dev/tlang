package tlang.internal;

import tlang.Entity;
import tlang.core.String;
import tlang.core.*;

public class TmplStringID extends TmplID {

    private final Null<ContextContent> context;

    private final String id;

    public TmplStringID(Null<ContextContent> context, String id) {
        this.context = context;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Null<ContextContent> getContext() {
        return context;
    }

    @Override
    public TmplID getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(TmplStringID.class);
    }

    @Override
    public Entity toEntity() {
        return null;
    }

}
