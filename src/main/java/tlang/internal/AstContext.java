package tlang.internal;

import tlang.core.Null;
import tlang.core.Type;

public interface AstContext {

    Null<ContextContent> getContext();

    Type getType();
}
