package tlang.internal;

import tlang.core.Entity;
import tlang.core.Null;
import tlang.core.String;
import tlang.core.Type;

public class TmplStringID extends TmplID {

    private final Null context;

    private final String id;

    public TmplStringID(Null context, String id) {
        this.context = context;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Null getContext() {
        return context;
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
        return ClassType.of(TmplStringID.class);
    }

    @Override
    public Entity toEntity() {
        return null;
    }

}
