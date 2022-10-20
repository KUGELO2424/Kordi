package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public class UserRegisterException extends RuntimeException{
    public UserRegisterException(String message) {
        super(message);
    }
}
