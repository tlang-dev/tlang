package tlang.internal;

import tlang.Entity;
import tlang.core.Model;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.Value;

public class TmplInterpretedId extends TmplID {

    private final Null<ContextContent> contextContent;

    private final Null<String> pre;

    private final NativeType<?> nativeType;
    private final Null<String> post;

    public TmplInterpretedId(Null<ContextContent> contextContent, Null<String> pre, NativeType<?> nativeType, Null<String> post) {
        this.contextContent = contextContent;
        this.pre = pre;
        this.nativeType = nativeType;
        this.post = post;
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
        return null;
    }

    @Override
    public Entity toEntity() {
        return null;
    }

    @Override
    public Model toModel() {
        return null;
    }

    public Null<String> getPre() {
        return pre;
    }

    public NativeType<?> getNativeType() {
        return nativeType;
    }

    public Null<String> getPost() {
        return post;
    }
}
