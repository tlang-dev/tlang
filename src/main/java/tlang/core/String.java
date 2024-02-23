package tlang.core;

import tlang.internal.ClassType;
import tlang.internal.ContextContent;

public class String implements Value<String> {

    public static final Type TYPE = ClassType.of(String.class);

    private final java.lang.String value;

    public String(java.lang.String value) {
        this.value = value;
    }

    public java.lang.String get() {
        return value;
    }

    public java.lang.String toString() {
        return value;
    }

    public Bool isEqual(String obj) {
        return new Bool(value.equals(obj.get()));
    }

    @Override
    public String getElement() {
        return this;
    }

    @Override
    public Type getType() {
        return TYPE;
    }

}
