package ATMSystem.exceptions;

public class InvalidPINException extends ATMSystemException {
    public InvalidPINException() {
        super("Your PIN is invalid! Please try again");
    }
}
