package tlang.mutable;

import tlang.core.Error;
import tlang.core.Long;
import tlang.core.String;
import tlang.core.*;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.core.func.MapFuncWithIndex;

public class List<T> {

    private T[] records;

    private int size;

    public List() {
        this.records = (T[]) new Object[10];
        this.size = 0;
    }

    public List(Long size) {
        this.records = (T[]) new Object[size.intValue()];
        this.size = 0;
    }

    public List(T... records) {
        this.records = (T[]) new Object[records.length + 10];
        this.size = records.length;
    }

    public FuncRet add(T record) {
        if (size == records.length) {
            var newRecords = (T[]) new Object[records.length * 2];
            System.arraycopy(records, 0, newRecords, 0, records.length);
            records = newRecords;
        }
        records[size++] = record;
        return FuncRet.VOID;
    }

    public FuncRet get(int index) {
        if (index < 0 || index >= size) {
            return FuncRet.error(new Error(new String("Index out of bound: " + index)));
        }
        return FuncRet.of(records[index]);
    }

    public <B> FuncRet map(MapFunc<T, B> func) {
        var list = new List<B>(new Long(size));
        for (int i = 0; i < size; i++) {
            list.add(func.apply(records[i]));
        }
        return FuncRet.of(list);
    }

    public <B> FuncRet mapWithIndex(MapFuncWithIndex<T, B> func) {
        var list = new List<B>(new Long(size));
        for (int i = 0; i < size; i++) {
            list.add(func.apply(records[i], new Long(i)));
        }
        return FuncRet.of(list);
    }

    public T[] getRecords() {
        return records;
    }

    public FuncRet toArray() {
        List<Set<T>> list = (List<Set<T>>) mapWithIndex((record, index) -> new Set<>(index.toStringValue(), record)).getFirst().get();
        return FuncRet.ofOneValue(new SetArray<>(list.getRecords()));
    }
}
