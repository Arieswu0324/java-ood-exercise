package ATMSystem.exceptions;

public class InvalidCardException extends ATMSystemException{
    public InvalidCardException() {
        super("card does not exist");
    }
}
