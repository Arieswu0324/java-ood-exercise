package taskmanagementsystem.exception;

public class EmptyUserException extends TaskManagementSystemException{
    public EmptyUserException() {
        super("User is empty!");
    }
}
