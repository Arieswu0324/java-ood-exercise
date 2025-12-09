package taskmanagementsystem.exception;

public class TaskNotExistException extends TaskManagementSystemException{
    public TaskNotExistException(String name) {
        super("Task" + name +"does not exist");
    }
}
