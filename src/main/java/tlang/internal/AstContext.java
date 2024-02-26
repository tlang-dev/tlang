package tlang.internal;

import tlang.core.Null;
import tlang.core.Type;

public interface AstContext {

    Null getContext();

    Type getType();
}
