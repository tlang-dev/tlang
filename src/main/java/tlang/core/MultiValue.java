package tlang.core;

public class MultiValue implements Value<MultiValue> {

    private final Value<?>[] values;

    public MultiValue(Value<?>... values) {
        this.values = values;
    }

    public Value<?>[] getValues() {
        return values;
    }

}
