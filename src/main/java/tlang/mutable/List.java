package tlang.mutable;

import tlang.core.Array;
import tlang.core.Long;
import tlang.core.Type;
import tlang.core.Value;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.core.func.MapFuncWithIndex;

public class List implements Value {

    private Value[] records;

    private int size;

    public List() {
        this.records = new Value[10];
        this.size = 0;
    }

    public List(Long size) {
        this.records = new Value[size.intValue()];
        this.size = 0;
    }

    public List(Value... records) {
        this.records = new Value[records.length + 10];
        this.size = records.length;
    }

    public FuncRet add(Value record) {
        if (size == records.length) {
            var newRecords = new Value[records.length * 2];
            System.arraycopy(records, 0, newRecords, 0, records.length);
            records = newRecords;
        }
        records[size++] = record;
        return FuncRet.VOID;
    }

    public FuncRet get(int index) {
        if (index < 0 || index >= size) {
            //return FuncRet.error(new Error(new String("Index out of bound: " + index)));
        }
        return FuncRet.of(records[index]);
    }

    public FuncRet map(MapFunc func) {
        var list = new List(new Long(size));
        for (int i = 0; i < size; i++) {
            list.add(func.apply(records[i]));
        }
        return FuncRet.of(list);
    }

    public FuncRet mapWithIndex(MapFuncWithIndex func) {
        var list = new List(new Long(size));
        for (int i = 0; i < size; i++) {
            list.add(func.apply(records[i], new Long(i)));
        }
        return FuncRet.of(list);
    }

    public Value[] getRecords() {
        return records;
    }

    public FuncRet toArray() {
//        List<Set<T>> list = (List<Set<T>>) mapWithIndex((record, index) -> new Set<>(index.toStringValue(), record)).getFirst().get();
//        return FuncRet.of(new Array<>(list.getRecords()));
        return new FuncRet(new Array(records));
    }

    @Override
    public List getValue() {
        return this;
    }

    @Override
    public Type getType() {
        return null;
    }

}
