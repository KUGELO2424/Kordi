package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public class CollectionItemNotFoundException extends RuntimeException{
    public CollectionItemNotFoundException(String message) {
        super(message);
    }
}
