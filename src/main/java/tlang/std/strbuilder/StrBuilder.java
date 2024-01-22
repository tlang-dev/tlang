package tlang.std.strbuilder;

public class StrBuilder {

    private final StringBuilder builder = new StringBuilder();

    public StrBuilder() {
    }

    public void add(String str) {
        builder.append(str);
    }

    public String toString() {
        return builder.toString();
    }

    public void ln() {
        builder.append("\n");
    }

    public void rln() {
        builder.append("\r\n");
    }

}
