package tlang.core.func;

import tlang.core.Error;
import tlang.core.Value;
import tlang.core.Void;

public interface OnError {

    FuncRet<Void> onError(Error error);

}
