package stackoverflow;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment {
    private final String id;
    private final LocalDateTime createTs;
    private final String content;
    private final User creator;

    public Comment(String content, User user) {
        this.content = content;
        this.creator = user;
        createTs = LocalDateTime.now();
        id = UUID.randomUUID().toString();
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreateTs() {
        return createTs;
    }

    public User getCreator() {
        return creator;
    }
}
