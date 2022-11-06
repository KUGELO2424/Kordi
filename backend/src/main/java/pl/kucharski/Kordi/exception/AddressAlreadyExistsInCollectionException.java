package pl.kucharski.Kordi.exception;

public class AddressAlreadyExistsInCollectionException extends RuntimeException{
    public AddressAlreadyExistsInCollectionException() {
    }

    public AddressAlreadyExistsInCollectionException(String message) {
        super(message);
    }
}
