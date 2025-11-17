package stackoverflow;


public class Answer extends Post {
    private final Question question;

    public Answer(Question question, String content, User creator) {
        super(content, creator);
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }


}
