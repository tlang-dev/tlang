package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;

public class Array<T> implements ImplicitMatch<Array<T>, Void, Void>, Value<Array<T>> {

    private final T[] records;

    public Array(T... records) {
        this.records = records;
    }

    public T[] getRecords() {
        return records;
    }

    public static <T> Array<T> empty() {
        return new Array<>();
    }

    public static <T> T get(Array<T> array, Int index) {
        return array.records[index.get()];
    }

    @Override
    public FuncRet<Void> match(ApplyVoidFunc<Array<T>> first, Null<ApplyVoidFunc<Void>> second, Null<ApplyVoidFunc<Void>> last) {
        if (records.length > 0) {
            first.apply(this);
        } else {
            second.ifNotNull(func -> func.apply(Void.VOID));
        }
        last.ifNotNull(func -> func.apply(Void.VOID));
        return FuncRet.VOID;
    }

    @Override
    public Array<T> value() {
        return this;
    }
}
