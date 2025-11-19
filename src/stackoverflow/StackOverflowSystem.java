package stackoverflow;

import stackoverflow.common.Commentable;
import stackoverflow.common.Votable;
import stackoverflow.entity.*;
import stackoverflow.enums.ReputationCredit;
import stackoverflow.enums.VoteType;
import stackoverflow.indexes.IndexSearch;
import stackoverflow.strategy.SearchStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StackOverflowSystem {
    private static volatile StackOverflowSystem instance;

    private final Map<String, Question> questions = new ConcurrentHashMap<>();

    private final Map<String, User> users = new ConcurrentHashMap<>();

    //user to List of question ids
    private final Map<User, List<String>> userIndex = new ConcurrentHashMap<>();

    //tag to List of question ids
    private final Map<Tag, List<String>> tagIndex = new ConcurrentHashMap<>();

    private final List<IndexSearch<?>> indexSearches = new CopyOnWriteArrayList<>();

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

    //只能在创建后被立即调用，否则在中途调用则需要对索引进行backfill
    public void addIndexSearches(List<IndexSearch<?>> indexSearches) {
        this.indexSearches.addAll(indexSearches);
    }

    public User createUser(String name, String email) {
        User user = new User(name, email);
        users.put(user.getId(), user);
        return user;
    }

    public Question createQuestion(String title, String content, User user, Set<Tag> tags) {
        Question question = new Question(title, content, user, tags);
        questions.put(question.getId(), question);
        assignScore(user, ReputationCredit.CREATE);
        addToIndexes(question);
        return question;
    }

    public void acceptAnswer(User user, Answer answer, Question question) {
        question.acceptAnswer(answer, user);
        assignScore(answer.getCreator(), ReputationCredit.ACCEPT);
    }


    public Answer answerQuestion(Question question, String content, User user) {
        Answer answer = new Answer(question, content, user);
        question.addAnswer(answer);
        assignScore(user, ReputationCredit.ANSWER);
        return answer;
    }

    public Comment addComment(Commentable post, String content, User user) {
        Comment comment = new Comment(content, user);
        post.addComment(comment);
        assignScore(user, ReputationCredit.COMMENT);
        return comment;
    }

    public void vote(Votable post, User user, VoteType type) {
        Vote vote = new Vote(user, type);
        post.addVote(vote);
        switch (type) {
            case VOTE_UP -> assignScore(user, ReputationCredit.VOTE_UP);
            case VOTE_DOWN -> assignScore(user, ReputationCredit.VOTE_DOWN);
        }
    }


    public List<Question> search(List<SearchStrategy> searchStrategies) {

        Set<Question> result = new HashSet<>();
        searchStrategies.forEach(strategy ->
                result.addAll(strategy.search(new ArrayList<>(questions.values()))));

        return new ArrayList<>(result);
    }

    private void assignScore(User user, ReputationCredit credit) {
        user.updateScore(credit.getCredit());
    }

    private void addToIndexes(Question question) {
        indexSearches.forEach(indexSearch -> indexSearch.addToIndex(question));
    }

    private void addToTagIndex(Question question, Set<Tag> tags) {
        tags.forEach(tag -> {
            tagIndex.computeIfAbsent(tag, k -> new CopyOnWriteArrayList<>()).add(question.getId());
        });
    }

    private void addToUserIndex(Question question, User user) {
        userIndex.computeIfAbsent(user, k -> new CopyOnWriteArrayList<>()).add(question.getId());
    }

    public List<Question> searchByUser(User user) {
        List<Question> result = new ArrayList<>();
        userIndex.get(user).forEach(it -> {
            result.add(questions.get(it));
        });
        return result;
    }


}
