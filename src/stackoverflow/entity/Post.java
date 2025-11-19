package stackoverflow.entity;

import stackoverflow.common.Commentable;
import stackoverflow.common.Votable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Post implements Commentable, Votable {

    private final String id;
    private final LocalDateTime createTs;
    private final String content;
    private final User creator;
    private final List<Comment> comments;
    private final Map<User, Vote> votes;

    public Post(String content, User creator) {
        this.id = UUID.randomUUID().toString();
        this.content = content;
        this.creator = creator;
        this.createTs = LocalDateTime.now();
        this.comments = new CopyOnWriteArrayList<>();
        this.votes = new ConcurrentHashMap<>();
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreateTs() {
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
