package kdg.be.warehouse.exceptions;

public class POConflictException extends RuntimeException {
    public POConflictException(String message) {
        super(message);
    }
}