package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

public class AddressNotFoundException extends RuntimeException{

    public AddressNotFoundException() {
        super();
    }

    public AddressNotFoundException(String message) {
        super(message);
    }

}
