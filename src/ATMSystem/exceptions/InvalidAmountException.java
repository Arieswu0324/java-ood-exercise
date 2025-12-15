package ATMSystem.exceptions;

public class InvalidAmountException extends ATMSystemException{
    public InvalidAmountException() {
        super("Invalid cash amount, only 100 denomination transactions are supported");
    }
}
