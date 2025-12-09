package taskmanagementsystem.exception;

public class InsufficientTaskInfoException extends TaskManagementSystemException {
    public InsufficientTaskInfoException(String field) {
        super("Field: " + field +" for task is missing");
    }
}
