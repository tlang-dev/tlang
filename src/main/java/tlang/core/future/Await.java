package tlang.core.future;

import tlang.core.Error;
import tlang.core.Null;
import tlang.core.Value;
import tlang.core.Void;
import tlang.core.func.Apply;
import tlang.core.func.ApplyFunc;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;

public class Await {

    public static <T> FuncRet<Future<T>> run(ApplyVoidFunc<Value<T>> funcOnComplete, ApplyVoidFunc<Error> funcOnError, Null<ApplyVoidFunc<Void>> funcFinally, ApplyFunc<T, Void> runnable) {
        var future = new Future<T>(funcOnComplete, funcOnError, funcFinally);
        new Thread(() -> {
//                runnable.apply(Void.VOID).onResult(future::onComplete).onError(future::onError).inAllCase(funcFinally.ifNotNull(value -> value.apply(Void.VOID)));
//                future.onComplete(value.get().get().value());
//                future.onError(error);
//                future.finallyFunc();
        }).start();
        return FuncRet.of(future);
    }
}
