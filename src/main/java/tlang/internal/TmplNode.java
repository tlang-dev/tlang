package tlang.internal;

import tlang.core.Entity;
import tlang.core.Type;

public interface TmplNode<T> extends Element<T>, Context {

    Type TYPE = ClassType.of(TmplNode.class);

    String name = TmplNode.class.getSimpleName();

    Entity toEntity();

//    Model toModel();

}
