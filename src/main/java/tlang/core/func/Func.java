package tlang.core.func;

import tlang.core.Array;
import tlang.core.Value;

public interface Func extends Value {

    FuncRet call(Array args);
}
