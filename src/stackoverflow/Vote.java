package stackoverflow;

import java.time.LocalDateTime;

public class Vote {
    private final User creator;
    private final LocalDateTime createTs;
    private final VoteType type;

    public Vote(User user, VoteType type) {
        this.creator = user;
        this.type = type;
        this.createTs = LocalDateTime.now();
    }

    public User getCreator() {
        return creator;
    }

}
