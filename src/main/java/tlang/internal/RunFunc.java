package tlang.internal;

import tlang.core.Array;
import tlang.core.func.FuncRet;

public interface RunFunc {

    FuncRet call(Array args);
}
