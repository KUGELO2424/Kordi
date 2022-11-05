package pl.kucharski.Kordi.exception;

public class UserAlreadyVerifiedException extends RuntimeException{

    public UserAlreadyVerifiedException() {
        super();
    }

    public UserAlreadyVerifiedException(String message) {
        super(message);
    }
}
