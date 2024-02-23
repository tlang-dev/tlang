package tlang.internal;

import tlang.Entity;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.Type;

public class TmplReplacedId extends TmplID {

    private final Null<ContextContent> context;

    private final Null<String> pre;

    private final TmplNode node;
    private final Null<String> post;

    public TmplReplacedId(Null<ContextContent> context, Null<String> pre, TmplNode node, Null<String> post) {
        this.context = context;
        this.pre = pre;
        this.node = node;
        this.post = post;
    }


    @Override
    public Entity toEntity() {
        return null;
    }

    public Null<ContextContent> getContext() {
        return context;
    }

    public Null<String> getPre() {
        return pre;
    }

    public TmplNode getNode() {
        return node;
    }

    public Null<String> getPost() {
        return post;
    }

    @Override
    public TmplID getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(TmplReplacedId.class);
    }
}
