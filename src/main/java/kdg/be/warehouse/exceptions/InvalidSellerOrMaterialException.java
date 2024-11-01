package kdg.be.warehouse.exceptions;

public class InvalidSellerOrMaterialException extends RuntimeException {
    public InvalidSellerOrMaterialException(String message) {
        super(message);
    }
}