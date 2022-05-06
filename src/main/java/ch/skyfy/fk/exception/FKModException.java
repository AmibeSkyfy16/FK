package ch.skyfy.fk.exception;

@SuppressWarnings("unused")
public class FKModException extends RuntimeException{

    public FKModException() {
        super();
    }

    public FKModException(String message) {
        super(message);
    }

    public FKModException(String message, Throwable cause) {
        super(message, cause);
    }

    public FKModException(Throwable cause) {
        super(cause);
    }

    public FKModException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
