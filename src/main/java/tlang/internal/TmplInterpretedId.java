package tlang.internal;

import tlang.core.Entity;
import tlang.core.Null;
import tlang.core.Type;

public class TmplInterpretedId extends TmplID {

    private final Null contextContent;

    private final Null pre;

    private final NativeType<?> nativeType;
    private final Null post;

    public TmplInterpretedId(Null contextContent, Null pre, NativeType<?> nativeType, Null post) {
        this.contextContent = contextContent;
        this.pre = pre;
        this.nativeType = nativeType;
        this.post = post;
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
        return null;
    }

    @Override
    public Entity toEntity() {
        return null;
    }

    public Null getPre() {
        return pre;
    }

    public NativeType<?> getNativeType() {
        return nativeType;
    }

    public Null getPost() {
        return post;
    }

    @Override
    public Null getContext() {
        return contextContent;
    }
}
