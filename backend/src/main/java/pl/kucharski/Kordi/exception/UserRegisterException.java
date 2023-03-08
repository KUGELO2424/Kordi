package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

public class UserRegisterException extends RuntimeException{
    public UserRegisterException(String message) {
        super(message);
    }
}
