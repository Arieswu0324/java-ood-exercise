package stackoverflow.entity;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Question extends Post {

    private final String title;
    private final List<Answer> answers;
    private final Set<Tag> tags;
    private final AtomicBoolean isAccepted;


    public Question(String title, String content, User creator, Set<Tag> tags) {
        super(content, creator);
        this.title = title;
        this.tags = new CopyOnWriteArraySet<>(tags);
        answers = new CopyOnWriteArrayList<>();
        isAccepted = new AtomicBoolean(false);
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public synchronized void acceptAnswer(Answer answer, User user) {
        if (!user.equals(getCreator())) {
            throw new IllegalArgumentException("Answer can only be accepted by the Question author");
        }
        if (isAccepted.get()) {
            throw new IllegalStateException(" Question already has an accepted answer, unable to accept");
        } else {
            isAccepted.set(true);
        }
        answer.setAccepted();
    }

    public String getTitle() {
        return title;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void addTags(Set<Tag> tags) {
        this.tags.addAll(tags);
    }
}
