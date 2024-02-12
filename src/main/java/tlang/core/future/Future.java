package tlang.core.future;

import tlang.core.Error;
import tlang.core.Null;
import tlang.core.Value;
import tlang.core.Void;
import tlang.core.func.Apply;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;

public class Future<T> implements Value<Future<T>> {

    private final ApplyVoidFunc<Value<T>> funcOnComplete;

    private final ApplyVoidFunc<Error> funcOnError;

    private final Null<ApplyVoidFunc<Void>> funcFinally;

    public Future(ApplyVoidFunc<Value<T>> funcOnComplete, ApplyVoidFunc<Error> funcOnError, Null<ApplyVoidFunc<Void>> funcFinally) {
        this.funcOnComplete = funcOnComplete;
        this.funcOnError = funcOnError;
        this.funcFinally = funcFinally;
    }

    public FuncRet<Void> onComplete(Value<T> value) {
        return funcOnComplete.apply(value);
    }

    public FuncRet<Void> onError(Error error) {
        return funcOnError.apply(error);
    }

    public FuncRet<Void> finallyFunc() {
        //funcFinally.ifNotNull(Apply::apply);
        return FuncRet.VOID;
    }

    @Override
    public Future<T> value() {
        return this;
    }
}
