package taskmanagementsystem.specification;

import taskmanagementsystem.entity.Task;

@FunctionalInterface
public interface SearchCriteria {
    boolean matches(Task task);

    //支持组合条件
    default SearchCriteria and(SearchCriteria other) {
        return task -> this.matches(task) && other.matches(task);
    }

    default SearchCriteria or(SearchCriteria other) {
        return task -> this.matches(task) || other.matches(task);
    }

    default SearchCriteria negate() {
        return task -> !this.matches(task);
    }
}
