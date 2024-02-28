package tlang.internal;

import tlang.core.Null;
import tlang.core.Type;

public interface Context {

    Null getContext();

    Type getType();
}
