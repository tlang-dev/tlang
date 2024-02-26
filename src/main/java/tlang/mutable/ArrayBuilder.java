package tlang.mutable;

import tlang.core.Array;
import tlang.core.Int;
import tlang.core.Value;

public class ArrayBuilder {

    private final Value[] array;

    private Int index = new Int(0);

    public ArrayBuilder(Int size) {
        this.array = new Value[size.get()];
    }

    public void add(Value value) {
        array[index.get()] = value;
        index = new Int(index.get() + 1);
    }

    public Array build() {
        return new Array(array);
    }

}
