package stackoverflow;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Post implements Commentable, Votable {

    private final long createTs;
    private final String content;
    private final User creator;
    private final List<Comment> comments;
    private final Map<User, Vote> votes;

    public Post(String content, User creator) {
        this.content = content;
        this.creator = creator;
        this.createTs = System.currentTimeMillis();
        this.comments = new CopyOnWriteArrayList<>();
        this.votes = new ConcurrentHashMap<>();
    }

    public long getCreateTs() {
        return createTs;
    }

    public String getContent() {
        return content;
    }

    public User getCreator() {
        return creator;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public Map<User, Vote> getVotes() {
        return votes;
    }

    @Override
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    @Override
    public void addVote(Vote vote) {
        if (!votes.containsKey(vote.getCreator())) {
            synchronized (this) {
                if (!votes.containsKey(vote.getCreator())) {
                    votes.put(vote.getCreator(), vote);
                }
            }
        } else {
            throw new IllegalArgumentException("不可重复投票！");
        }
    }
}
