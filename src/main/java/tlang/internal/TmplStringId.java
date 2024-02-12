package tlang.internal;

import tlang.Entity;
import tlang.core.Model;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.Value;

public class TmplStringId extends TmplId {

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
    public TmplId getElement() {
        return this;
    }

    @Override
    public String getType() {
        return new String(getClass().getSimpleName());
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
