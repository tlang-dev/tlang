package tlang.core.func;

import tlang.core.Value;

public interface Apply extends Value {

    FuncRet apply();
}
