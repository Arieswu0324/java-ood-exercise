package taskmanagementsystem.exception;

public class UnsupportedUserOperationException extends RuntimeException {
    public UnsupportedUserOperationException() {
        super("User is not the task assignee, not allowed to operate");
    }
}
