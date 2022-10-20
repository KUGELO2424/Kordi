package pl.kucharski.Kordi.exception;

/**
 * @author Grzegorz Kucharski 229932@edu.p.lodz.pl
 */

public class CollectionNotFoundException extends RuntimeException{

    public CollectionNotFoundException() {
        super();
    }

    public CollectionNotFoundException(String message) {
        super(message);
    }

}
