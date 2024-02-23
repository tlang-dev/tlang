package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.mutable.ArrayBuilder;

public class SetArray<T> implements ImplicitMatch<SetArray<T>, Void, Void> {

    private final Set<T>[] records;

    public SetArray(Set<T>... records) {
        this.records = records;
    }

    public Set<T>[] getRecords() {
        return records;
    }

    public static <T> SetArray<T> empty() {
        return new SetArray<>();
    }


    public static <T> Value<T> get(Set<T>[] records, String key) {
        for (Set<T> record : records) {
            if (record.getKey().equals(key)) {
                return record.getValue();
            }
        }
        throw new RuntimeException("Key not found: " + key);
    }

    public static <T> Value<T> get(Set<T>[] records, Long index) {
        if (index.get() < 0 || index.get() >= records.length) {
            throw new RuntimeException("Index out of bound: " + index);
        }
        return records[(int) index.get()].getValue();
    }

//    public static <T> Array<T> getValues(Set<T>[] records) {
//        var array = (T[]) new Object[records.length];
//        for (int i = 0; i < records.length; i++) {
////            array[i] = records[i].getValue();
//        }
//        return new Array<>(array);
//    }

    public static Array<String> getKeys(Set<?>[] records) {
        var array = new String[records.length];
        for (int i = 0; i < records.length; i++) {
            array[i] = records[i].getKey();
        }
        return new Array<>(array);
    }

    public static <T, B> Array<B> map(Set<T>[] records, MapFunc<T, B> func) {
        var array = new ArrayBuilder<B>(new Int(records.length));
        for (Set<T> record : records) {
            array.add(func.apply(record.getValue()));
        }
        return array.build();
    }

    @Override
    public FuncRet match(ApplyVoidFunc<SetArray<T>> first, Null<ApplyVoidFunc<Void>> second, Null<ApplyVoidFunc<Void>> last) {
        if (records.length > 0) {
            first.apply(this);
        } else {
            second.ifNotNull(func -> func.apply(Void.VOID));
        }
        last.ifNotNull(func -> func.apply(Void.VOID));
        return FuncRet.VOID;
    }
}
