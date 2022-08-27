package pl.kucharski.Kordi.exception;

public class CollectionNotFound extends RuntimeException{

    public CollectionNotFound() {
        super();
    }

    public CollectionNotFound(String message) {
        super(message);
    }

    public CollectionNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectionNotFound(Throwable cause) {
        super(cause);
    }
}
