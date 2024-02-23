package tlang.internal;

import tlang.Entity;
import tlang.core.Model;
import tlang.core.Type;

public interface TmplNode<T> extends Element<T>, AstContext {

    Type TYPE = ClassType.of(TmplNode.class);

    String name = TmplNode.class.getSimpleName();

    Entity toEntity();

//    Model toModel();

}
