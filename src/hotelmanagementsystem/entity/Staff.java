package hotelmanagementsystem.entity;

import java.util.UUID;

public class Staff extends User {
    private final String staffId;

    public Staff(String name, String email) {
        super(name, email);
        this.staffId = UUID.randomUUID().toString();
    }

    public String getStaffId() {
        return staffId;
    }
}
