package taskmanagementsystem.exception;

public class UnsupportedStatusException extends TaskManagementSystemException {
    public UnsupportedStatusException(String status) {
        super("Not able to operate at status: " + status);
    }
}
