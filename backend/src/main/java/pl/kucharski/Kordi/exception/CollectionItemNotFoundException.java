package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public class CollectionItemNotFoundException extends RuntimeException{

    public CollectionItemNotFoundException() {
        super();
    }

    public CollectionItemNotFoundException(String message) {
        super(message);
    }

    public CollectionItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectionItemNotFoundException(Throwable cause) {
        super(cause);
    }
}
