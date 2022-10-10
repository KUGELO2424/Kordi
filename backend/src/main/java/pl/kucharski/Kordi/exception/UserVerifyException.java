package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public class UserVerifyException extends RuntimeException{
    public UserVerifyException(String message) {
        super(message);
    }
}
