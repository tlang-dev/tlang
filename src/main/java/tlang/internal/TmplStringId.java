package tlang.internal;

import tlang.Entity;
import tlang.core.String;
import tlang.core.*;

public class TmplStringId extends TmplID {

    private final Null<ContextContent> context;

    private final String id;

    public TmplStringId(Null<ContextContent> context, String id) {
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
    public Value<?> deepCopy() {
        return new TmplStringId(context, new String(id.get()));
    }

    @Override
    public TmplID getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(TmplStringId.class);
    }

    @Override
    public Entity toEntity() {
        return null;
    }

    @Override
    public Model toModel() {
        return null;
    }
}
