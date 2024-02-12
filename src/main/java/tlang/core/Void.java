package tlang.core;

public class Void implements Value<Void> {


    public static final Void VOID = new Void();

    private Void() {
    }

    @Override
    public Void getElement() {
        return this;
    }

    @Override
    public String getType() {
        return null;
    }
}
