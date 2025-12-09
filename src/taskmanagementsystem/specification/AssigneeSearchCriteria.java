package taskmanagementsystem.specification;

import taskmanagementsystem.entity.Task;
import taskmanagementsystem.entity.User;

public class AssigneeSearchCriteria implements SearchCriteria {
    private final User assignee;

    public AssigneeSearchCriteria(User assignee) {
        if (assignee == null) {
            throw new IllegalArgumentException("Assignee cannot be null");
        }
        this.assignee = assignee;
    }

    @Override
    public boolean matches(Task task) {
        return assignee.equals(task.getAssignee());
    }
}
