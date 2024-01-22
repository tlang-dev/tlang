package tlang.core;

public class Error {

    private final String code;

    private final Null<String> message;

    private final Error[] stackTrace;

    public Error(String code) {
        this.code = code;
        this.message = Null.empty();
        this.stackTrace = new Error[0];
    }

    public Error(String code, Null<String> message) {
        this.code = code;
        this.message = message;
        this.stackTrace = new Error[0];
    }

    public Error(String code, Null<String> message, Error[] stackTrace) {
        this.code = code;
        this.message = message;
        this.stackTrace = stackTrace;
    }

    public Error(String code, Error[] stackTrace) {
        this.code = code;
        this.message = Null.empty();
        this.stackTrace = stackTrace;
    }

    public String getCode() {
        return code;
    }

    public Null<String> getMessage() {
        return message;
    }

    public Error[] getStackTrace() {
        return stackTrace;
    }
}
