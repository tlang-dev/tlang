package tlang.internal;

import tlang.core.Entity;
import tlang.core.Null;
import tlang.core.Type;

public class TmplBlockId extends TmplID {

    private final Null context;

    private final AnyTmplBlock<?> block;

    public TmplBlockId(Null context, AnyTmplBlock<?> block) {
        this.context = context;
        this.block = block;
    }

    @Override
    public TmplID getValue() {
        return this;
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
    public Null getContext() {
        return context;
    }
}
