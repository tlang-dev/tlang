package tlang.core.func;

import tlang.core.Long;
import tlang.core.Value;

public interface MapFuncWithIndex {

    Value apply(Value value, Long index);
}
