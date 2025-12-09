package taskmanagementsystem.entity;

import taskmanagementsystem.exception.UnsupportedUserOperationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private final String name;
    private final String email;

    private final Map<String, Task> history = new ConcurrentHashMap<>();


    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public void addToHistory(Task task) {
        if (!task.getAssignee().equals(this)) {
            throw new UnsupportedUserOperationException();
        }
        history.put(task.getTitle(), task);
    }

    public void removeFromHistory(String name) {
        history.remove(name);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<Task> getHistory() {
        return new LinkedList<>(history.values());
    }

    //hashcode for map...
    //equals for compare...

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        User other = (User) obj;

        if (!Objects.equals(other.name, name)) {
            return false;
        }

        if (!Objects.equals(other.email, email)) {
            return false;
        }

        return true;
    }


}
