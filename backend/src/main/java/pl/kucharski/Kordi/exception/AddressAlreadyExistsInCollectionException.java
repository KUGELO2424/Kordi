package pl.kucharski.Kordi.exception;

public class AddressAlreadyExistsInCollectionException extends RuntimeException{

    public AddressAlreadyExistsInCollectionException() {
    }

    public AddressAlreadyExistsInCollectionException(String message) {
        super(message);
    }

    public AddressAlreadyExistsInCollectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddressAlreadyExistsInCollectionException(Throwable cause) {
        super(cause);
    }
}
