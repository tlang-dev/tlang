package tlang.core.future;

import tlang.core.Error;
import tlang.core.Null;
import tlang.core.Type;
import tlang.core.Value;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.internal.ClassType;

public class Future implements Value {

    private final ApplyVoidFunc funcOnComplete;

    private final ApplyVoidFunc funcOnError;

    private final Null funcFinally;

    public Future(ApplyVoidFunc funcOnComplete, ApplyVoidFunc funcOnError, Null funcFinally) {
        this.funcOnComplete = funcOnComplete;
        this.funcOnError = funcOnError;
        this.funcFinally = funcFinally;
    }

    public FuncRet onComplete(Value value) {
        return funcOnComplete.apply(value);
    }

    public FuncRet onError(Error error) {
        return funcOnError.apply(error);
    }

    public FuncRet finallyFunc() {
        //funcFinally.ifNotNull(Apply::apply);
        return FuncRet.VOID;
    }

    @Override
    public Future getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(Future.class);
    }
}
