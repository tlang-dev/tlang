package tlang.core;

public interface Value {

    default Int compareTo(Value other) {
        return new Int(0);
    }

    Value getValue();

    Type getType();
}
