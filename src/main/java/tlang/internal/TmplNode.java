package tlang.internal;

import tlang.Entity;
import tlang.core.Model;

public interface TmplNode<T> extends DeepCopy, AstContext {

    String name = TmplNode.class.getSimpleName();

    Entity toEntity();

    Model toModel();
}
