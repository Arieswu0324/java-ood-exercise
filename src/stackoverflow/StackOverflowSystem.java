package stackoverflow;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StackOverflowSystem {
    private static volatile StackOverflowSystem instance;

    private final List<Question> questions = new CopyOnWriteArrayList<>();

    private final Map<User, List<Question>> userIndex = new ConcurrentHashMap<>();

    private final Map<Tag, List<Question>> tagIndex = new ConcurrentHashMap<>();

    private StackOverflowSystem() {
    }

    public static StackOverflowSystem getInstance() {
        if (instance == null) {
            synchronized (StackOverflowSystem.class) {
                if (instance == null) {
                    instance = new StackOverflowSystem();
                }
            }
        }
        return instance;
    }

    public Question createQuestion(String title, String content, User user, Set<Tag> tags) {
        Question question = new Question(title, content, user, tags);
        questions.add(question);
        assignScore(user, ActivityCredit.CREATE);
        addToUserIndex(question, user);
        addToTagIndex(question, tags);
        return question;
    }


    public Answer answerQuestion(Question question, String content, User user) {
        Answer answer = new Answer(question, content, user);
        question.addAnswer(answer);
        assignScore(user, ActivityCredit.ANSWER);
        return answer;
    }

    public Comment addComment(Commentable post, String content, User user) {
        Comment comment = new Comment(content, user);
        post.addComment(comment);
        assignScore(user, ActivityCredit.COMMENT);
        return comment;
    }

    public void vote(Votable post, User user) {
        Vote vote = new Vote(user);
        post.addVote(vote);
        assignScore(user, ActivityCredit.VOTE);
    }

    public void addTags(Question question, Set<Tag> tags) {
        question.addTags(tags);
        addToTagIndex(question, tags);
    }

    public Optional<List<Question>> searchByUser(User user) {
        return Optional.ofNullable(userIndex.get(user));
    }

    public Optional<List<Question>> searchByTag(Set<Tag> tags) {
        Set<Question> questionSet = new HashSet<>();
        tags.forEach(tag -> {
            List<Question> questions1 = tagIndex.get(tag);
            if (questions1 != null) {
                questionSet.addAll(questions1);
            }
        });
        List<Question> list = new ArrayList<>(questionSet);
        return Optional.of(list);
    }

    private void assignScore(User user, ActivityCredit credit) {
        user.updateScore(credit.getCredit());
    }

    private void addToTagIndex(Question question, Set<Tag> tags) {
        tags.forEach(tag -> {
            tagIndex.computeIfAbsent(tag, k -> new CopyOnWriteArrayList<>()).add(question);
        });
    }

    private void addToUserIndex(Question question, User user) {
        userIndex.computeIfAbsent(user, k -> new CopyOnWriteArrayList<>()).add(question);
    }

}
