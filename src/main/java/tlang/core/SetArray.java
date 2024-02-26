package tlang.core;

import tlang.core.func.ApplyVoidFunc;
import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.mutable.ArrayBuilder;

public class SetArray  {

    private final Set[] records;

    public SetArray(Set... records) {
        this.records = records;
    }

    public Set[] getRecords() {
        return records;
    }

    public static SetArray empty() {
        return new SetArray();
    }


    public static  Value get(Set[] records, String key) {
        for (Set record : records) {
            if (record.getKey().equals(key)) {
                return record.getValue();
            }
        }
        throw new RuntimeException("Key not found: " + key);
    }

    public static  Value get(Set[] records, Long index) {
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

    public static Array getKeys(Set[] records) {
        var array = new String[records.length];
        for (int i = 0; i < records.length; i++) {
            array[i] = records[i].getKey();
        }
        return new Array(array);
    }

    public static  Array map(Set[] records, MapFunc func) {
        var array = new ArrayBuilder(new Int(records.length));
        for (Set record : records) {
            array.add(func.apply(record.getValue()));
        }
        return array.build();
    }


}
