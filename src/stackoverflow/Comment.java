package stackoverflow;

public class Comment {
    private final long createTs;
    private final String content;
    private final User creator;

    public Comment(String content, User user){
        this.content = content;
        this.creator = user;
        createTs = System.currentTimeMillis();
    }

    public String getContent() {
        return content;
    }

    public long getCreateTs() {
        return createTs;
    }

    public User getCreator() {
        return creator;
    }
}
