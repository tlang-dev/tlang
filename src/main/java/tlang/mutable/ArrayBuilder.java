package tlang.mutable;

import tlang.core.Array;
import tlang.core.Int;
import tlang.core.Value;

public class ArrayBuilder<T> {

    private final Value<T>[] array;

    private Int index = new Int(0);

    public ArrayBuilder(Int size) {
        this.array = (Value<T>[]) new Object[size.get()];
    }

    public void add(Value<T> value) {
        array[index.get()] = value;
        index = new Int(index.get() + 1);
    }

    public Array<T> build() {
        return new Array<>(array);
    }

}
