package tlang.internal;

import tlang.Entity;
import tlang.core.Model;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.Value;

public class TmplReplacedId extends TmplID {

    private final Null<ContextContent> context;

    private final Null<String> pre;

    private final TmplNode<?> node;
    private final Null<String> post;

    public TmplReplacedId(Null<ContextContent> context, Null<String> pre, TmplNode<?> node, Null<String> post) {
        this.context = context;
        this.pre = pre;
        this.node = node;
        this.post = post;
    }

    @Override
    public Value<?> deepCopy() {
        return null;
    }

    @Override
    public TmplID getElement() {
        return null;
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

    public Null<ContextContent> getContext() {
        return context;
    }

    public Null<String> getPre() {
        return pre;
    }

    public TmplNode<?> getNode() {
        return node;
    }

    public Null<String> getPost() {
        return post;
    }
}
