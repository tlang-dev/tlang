package tlang.core.func;

import dev.tlang.tlang.astbuilder.context.ContextContent;
import dev.tlang.tlang.interpreter.Value;
import scala.Option;
import tlang.core.Error;
import tlang.core.Null;
import tlang.core.Type;

public class FuncRet implements Value<FuncRet> {

    public static final FuncRet VOID = new FuncRet();

    private final Type<?>[] args;

    private final Null<Error> error;

    public FuncRet() {
        this.args = new Type[0];
        this.error = Null.empty();
    }

    public FuncRet(Type<?>... args) {
        this.args = args;
        this.error = Null.empty();
    }

    public FuncRet(Object... args) {
        this.args = new Type[args.length];
        for (int i = 0; i < args.length; i++) {
            this.args[i] = new Type<>(args[i]);
        }
        this.error = Null.empty();
    }

    public FuncRet(Error error) {
        this.args = new Type[0];
        this.error = Null.of(error);
    }

    public Type<?>[] get() {
        return args;
    }

    public FuncRet onResult(OnResult onResult) {
        if (!error.isNull()) {
            onResult.onResult(args);
        }
        return this;
    }

    public FuncRet onError(OnError onError) {
        error.ifNotNull(onError::onError);
        return this;
    }

    public FuncRet inAllCase(ApplyFunc<Type<?>[]> func) {
        func.apply(args);
        return FuncRet.VOID;
    }

    public <B> FuncRet map(MapFunc<Type<?>, B> func) {
        var array = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            array[i] = func.apply(args[i]);
        }
        return new FuncRet(array);
    }

    public boolean isVoid() {
        return args.length == 0 && error.isNull();
    }

    public static FuncRet of(Object... args) {
        return new FuncRet(args);
    }

    public static FuncRet of(Type<?>... args) {
        return new FuncRet(args);
    }

    public static FuncRet error(Error error) {
        return new FuncRet(error);
    }

    public static FuncRet error(String code, String message) {
        return new FuncRet(new Error(code, Null.of(message)));
    }

    public static FuncRet error(String code) {
        return new FuncRet(new Error(code, Null.empty()));
    }

    public static <T extends Throwable> FuncRet error(T e) {
        return new FuncRet(new Error(e.getClass().getSimpleName(), Null.of(e.getMessage())));
    }

    @Override
    public Option<ContextContent> getContext() {
        return Option.empty();
    }

    @Override
    public int compareTo(Value<FuncRet> value) {
        return 0;
    }

    @Override
    public FuncRet getElement() {
        return this;
    }

    @Override
    public String getType() {
        return FuncRet.class.getSimpleName();
    }
}
