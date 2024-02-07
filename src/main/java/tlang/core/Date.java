package tlang.core;

import java.time.Instant;

public class Date {

    private final Instant value;

    public Date(Instant value) {
        this.value = value;
    }

    public Instant get() {
        return value;
    }
}
