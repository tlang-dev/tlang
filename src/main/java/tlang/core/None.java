package tlang.core;

public class None implements Value {

    @Override
    public Value getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return null;
    }
}
