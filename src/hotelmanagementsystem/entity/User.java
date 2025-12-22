package hotelmanagementsystem.entity;

import java.util.UUID;

public class User {
    private final String id;
    private  String name;
    private  String email;

    public User(String name, String email){
        this.name = name;
        this.email = email;
        this.id = UUID.randomUUID().toString();
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }
}
