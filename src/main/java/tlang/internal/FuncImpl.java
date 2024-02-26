package tlang.internal;

import tlang.core.Array;
import tlang.core.Type;
import tlang.core.func.Func;
import tlang.core.func.FuncRet;

public class FuncImpl implements Func {

    private final Class<?> clazz;

    private final RunFunc runFunc;

    public FuncImpl(Class<?> clazz, RunFunc runFunc) {
        this.clazz = clazz;
        this.runFunc = runFunc;
    }

    @Override
    public Func getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(clazz);
    }

    @Override
    public FuncRet call(Array args) {
        return runFunc.call(args);
    }

}
