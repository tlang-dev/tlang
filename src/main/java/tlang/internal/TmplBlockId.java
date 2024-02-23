package tlang.internal;

import tlang.Entity;
import tlang.core.Model;
import tlang.core.Null;
import tlang.core.Type;
import tlang.core.Value;

public class TmplBlockId extends TmplID {

    private final Null<ContextContent> context;

    private final AnyTmplBlock<?> block;

    public TmplBlockId(Null<ContextContent> context, AnyTmplBlock<?> block) {
        this.context = context;
        this.block = block;
    }

    @Override
    public TmplID getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(TmplBlockId.class);
    }

    @Override
    public Entity toEntity() {
        return null;
    }

    public AnyTmplBlock<?> getBlock() {
        return block;
    }

    @Override
    public Null<ContextContent> getContext() {
        return context;
    }
}
