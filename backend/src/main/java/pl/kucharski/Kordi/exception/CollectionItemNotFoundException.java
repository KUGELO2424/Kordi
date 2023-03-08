package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski gelo2424@wp.pl
 */

public class CollectionItemNotFoundException extends RuntimeException{
    public CollectionItemNotFoundException(String message) {
        super(message);
    }
}
