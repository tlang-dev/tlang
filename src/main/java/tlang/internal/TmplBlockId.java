package tlang.internal;

import tlang.Entity;
import tlang.core.Model;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.Value;

public class TmplBlockId extends TmplID {

    private final Null<ContextContent> context;

    private final AnyTmplBlock<?> block;

    public TmplBlockId(Null<ContextContent> context, AnyTmplBlock<?> block) {
        this.context = context;
        this.block = block;
    }

    @Override
    public Value<?> deepCopy() {
        return null;
    }

    @Override
    public TmplID getElement() {
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
