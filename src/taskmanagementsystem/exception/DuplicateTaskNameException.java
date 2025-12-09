package taskmanagementsystem.exception;

public class DuplicateTaskNameException extends TaskManagementSystemException {
    public DuplicateTaskNameException() {
        super("Task Name already exists, please try another one");
    }
}
