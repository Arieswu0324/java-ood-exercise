package stackoverflow;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class Question extends Post {

    private final String title;
    private final List<Answer> answers;
    private final Set<Tag> tags;


    public Question(String title, String content, User creator, Set<Tag> tags) {
        super(content, creator);
        this.title = title;
        this.tags = new CopyOnWriteArraySet<>(tags);
        answers = new CopyOnWriteArrayList<>();
    }

    public void addAnswer(Answer answer) {
        answers.add(answer);
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
