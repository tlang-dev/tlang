package tlang.internal;

import tlang.core.Entity;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.Type;

public class TmplReplacedId extends TmplID {

    private final Null context;

    private final Null pre;

    private final TmplNode node;
    private final Null post;

    public TmplReplacedId(Null context, Null pre, TmplNode node, Null post) {
        this.context = context;
        this.pre = pre;
        this.node = node;
        this.post = post;
    }


    @Override
    public Entity toEntity() {
        return null;
    }

    public Null getContext() {
        return context;
    }

    public Null getPre() {
        return pre;
    }

    public TmplNode getNode() {
        return node;
    }

    public Null getPost() {
        return post;
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
        return ClassType.of(TmplReplacedId.class);
    }
}
