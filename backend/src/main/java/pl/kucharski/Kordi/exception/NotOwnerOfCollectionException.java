package pl.kucharski.Kordi.exception;

public class NotOwnerOfCollectionException extends RuntimeException{

    public NotOwnerOfCollectionException(String message) {
        super(message);
    }
}
