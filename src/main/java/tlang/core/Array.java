package tlang.core;

import tlang.core.func.FuncRet;
import tlang.core.func.MapFunc;
import tlang.internal.ClassType;
import tlang.mutable.List;

public class Array implements Entity {

    public static final Type TYPE = ClassType.of(Array.class);

    private final Value[] records;

    public Array(Value[] records) {
        this.records = records;
    }

    public Value[] getRecords() {
        return records;
    }

    public Int length() {
        return Array.length(this);
    }

    public Value get(Int index) {
        return records[index.get()];
    }

    public FuncRet map(MapFunc func) {
        return Array.map(this, func);
    }

    public static Array empty() {
        return new Array(new Value[0]);
    }

    public static  Value get(Array array, Int index) {
        return array.records[index.get()];
    }

    public static FuncRet map(Array array, MapFunc func) {
        var list = new List(new Long(array.records.length));
        for (int i = 0; i < array.length().get(); i++) {
//            list.add(func.apply(array.getRecords()[i]));
        }
        return FuncRet.of(list);
    }

    @Override
    public Array getValue() {
        return this;
    }

    @Override
    public Null getAttr(String name) {
        return null;
    }

 /*   @Override
    public Bool hasAttrs() {
        return null;
    }

    @Override
    public Bool exists(String name) {
        return null;
    }*/

    @Override
    public Null getAttr(Int index) {
        return null;
    }

   /* @Override
    public Bool exists(Int index) {
        return null;
    }

    @Override
    public FuncRet call(String name, Array args) {
        return null;
    }

    @Override
    public FuncRet call(Int index, Array args) {
        return null;
    }*/

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Null getModel() {
        return null;
    }

    public static Int length(Array array) {
        return new Int(array.records.length);
    }

}
