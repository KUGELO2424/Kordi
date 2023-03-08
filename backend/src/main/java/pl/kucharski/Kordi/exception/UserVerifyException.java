package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

public class UserVerifyException extends RuntimeException{
    public UserVerifyException(String message) {
        super(message);
    }
}
