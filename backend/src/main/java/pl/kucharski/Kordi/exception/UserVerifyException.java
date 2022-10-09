package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

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
