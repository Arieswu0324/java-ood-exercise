package stackoverflow;

public class Vote {
    private final User creator;
    private final long createTs;

    public Vote(User user) {
        this.creator = user;
        this.createTs = System.currentTimeMillis();
    }

    public User getCreator(){
        return creator;
    }

    public long getCreateTs() {
        return createTs;
    }
}
