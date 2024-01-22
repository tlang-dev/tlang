package tlang.core.func;

import tlang.core.Type;

public interface RefFunc {

    FuncRet call(Type<?>... args);
}
