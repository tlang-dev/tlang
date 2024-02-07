package tlang.core;

import tlang.core.func.ApplyFunc;

public class Array<T> implements ImplicitMatch<Array<T>, Void, Void> {

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
    public void match(ApplyFunc<Array<T>> first, Null<ApplyFunc<Void>> second, Null<ApplyFunc<Void>> last) {
        if (records.length > 0) {
            first.apply(this);
        } else {
            second.ifNotNull(func -> func.apply(Void.VOID));
        }
        last.ifNotNull(func -> func.apply(Void.VOID));
    }
}
