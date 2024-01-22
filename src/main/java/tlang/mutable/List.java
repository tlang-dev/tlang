package tlang.mutable;

public class List<T> {

    private T[] records;

    private int size;

    public List() {
        this.records = (T[]) new Object[10];
        this.size = 0;
    }

    public List(T... records) {
        this.records = (T[]) new Object[records.length + 10];
        this.size = records.length;
    }

}
