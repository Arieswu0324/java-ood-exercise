package stackoverflow.entity;


import java.util.concurrent.atomic.AtomicBoolean;

public class Answer extends Post {
    private final Question question;
    private final AtomicBoolean accepted;

    public Answer(Question question, String content, User creator) {
        super(content, creator);
        this.question = question;
        accepted = new AtomicBoolean(false);
    }

    public Question getQuestion() {
        return question;
    }

    public void setAccepted() {
        accepted.set(true);

    }


}
