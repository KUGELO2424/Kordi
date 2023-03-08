package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

public class CollectionNotFoundException extends RuntimeException{

    public CollectionNotFoundException() {
        super();
    }

    public CollectionNotFoundException(String message) {
        super(message);
    }

}
