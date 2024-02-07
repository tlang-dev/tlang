package tlang.mutable;

import tlang.core.Array;
import tlang.core.Int;

public class ArrayBuilder<T> {

    private final T[] array;

    private Int index = new Int(0);

    public ArrayBuilder(Int size) {
        this.array = (T[]) new Object[size.get()];
    }

    public void add(T value) {
        array[index.get()] = value;
        index = new Int(index.get() + 1);
    }

    public Array<T> build() {
        return new Array<>(array);
    }

}
