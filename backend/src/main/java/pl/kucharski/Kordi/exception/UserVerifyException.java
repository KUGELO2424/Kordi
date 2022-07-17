package pl.kucharski.Kordi.exception;

public class UserVerifyException extends RuntimeException{

    public UserVerifyException() {
        super();
    }

    public UserVerifyException(String message) {
        super(message);
    }

    public UserVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserVerifyException(Throwable cause) {
        super(cause);
    }
}
