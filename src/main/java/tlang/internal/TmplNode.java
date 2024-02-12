package tlang.internal;

import tlang.Entity;
import tlang.core.Model;
import tlang.core.Value;

public interface TmplNode<T> extends Value<T>, DeepCopy {

    String name = TmplNode.class.getSimpleName();

    Entity toEntity();

    Model toModel();
}
