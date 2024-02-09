package tlang.core;

public class Void implements Value<Void> {
    @Override
    public Void value() {
        return this;
    }

    public static final Void VOID = new Void();

    private Void() {
    }
}
