package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.internal.ClassType;
import tlang.internal.ContextContent;
import tlang.mutable.List;

public class Array<T> implements ImplicitMatch<Array<T>, Void, Void>, Value<Array<T>> {

    public static final Type TYPE = ClassType.of(Array.class);

    private final T[] records;

    public Array(T... records) {
        this.records = records;
    }

    public T[] getRecords() {
        return records;
    }

    public Int length() {
        return Array.length(this);
    }

    public <B extends Value<B>> FuncRet<List<B>> map(MapFunc<T, B> func) {
        return Array.map(this, func);
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

    public static <T, B extends Value<B>> FuncRet<List<B>> map(Array<T> array, MapFunc<T, B> func) {
        var list = new List<B>(new Long(array.records.length));
        for (int i = 0; i < array.length().get(); i++) {
            list.add(func.apply(array.getRecords()[i]));
        }
        return FuncRet.of(list);
    }

    @Override
    public Array<T> getElement() {
        return this;
    }

    @Override
    public Null<ContextContent> getContext() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }

    public static Int length(Array<?> array) {
        return new Int(array.records.length);
    }

}
