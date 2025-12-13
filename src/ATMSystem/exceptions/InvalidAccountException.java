package ATMSystem.exceptions;

public class InvalidAccountException extends RuntimeException {
    public InvalidAccountException() {
        super("Invalid Account Exception");
    }
}
