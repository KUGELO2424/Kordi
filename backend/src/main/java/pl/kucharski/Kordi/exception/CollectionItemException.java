package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public class CollectionItemException extends RuntimeException{

    public CollectionItemException() {
        super();
    }

    public CollectionItemException(String message) {
        super(message);
    }

    public CollectionItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectionItemException(Throwable cause) {
        super(cause);
    }
}
