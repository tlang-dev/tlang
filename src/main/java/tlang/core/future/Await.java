package tlang.core.future;

import tlang.core.Null;
import tlang.core.func.ApplyFunc;
import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;

public class Await {

    public static FuncRet run(ApplyVoidFunc funcOnComplete, ApplyVoidFunc funcOnError, Null funcFinally, ApplyFunc runnable) {
        var future = new Future(funcOnComplete, funcOnError, funcFinally);
        new Thread(() -> {
//                runnable.apply(Void.VOID).onResult(future::onComplete).onError(future::onError).inAllCase(funcFinally.ifNotNull(value -> value.apply(Void.VOID)));
//                future.onComplete(value.get().get().value());
//                future.onError(error);
//                future.finallyFunc();
        }).start();
        return FuncRet.of(future);
    }
}
