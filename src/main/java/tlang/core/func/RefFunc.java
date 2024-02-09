package tlang.core.func;

import tlang.core.Value;

public interface RefFunc {

    FuncRet<?> call(Value<?>... args);
}
