package taskmanagementsystem.exception;

import taskmanagementsystem.service.TaskManagementSystem;

public class UnsupportedSearchOperationException extends TaskManagementSystemException {
    public UnsupportedSearchOperationException(){
        super("search condition not supported");
    }
}
