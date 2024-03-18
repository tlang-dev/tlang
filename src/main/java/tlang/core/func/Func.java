package tlang.core.func;

import tlang.core.Array;
import tlang.core.Type;
import tlang.core.Value;
import tlang.internal.ClassType;

public class Func implements Apply {

    private final FuncDef funcDef;

    private final Array args;

    public Func(FuncDef funcDef, Array args) {
        this.funcDef = funcDef;
        this.args = args;
    }

    public static Func from(FuncDef func, Value... values) {
        return new Func(func, new Array(values));
    }

    @Override
    public Value getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return ClassType.of(this.getClass());
    }

    @Override
    public FuncRet apply() {
        return funcDef.call(args);
    }
}
