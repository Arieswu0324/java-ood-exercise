package stackoverflow;

import java.util.List;

public class UserSearchStrategy implements SearchStrategy {
    private final User user;

    public UserSearchStrategy(User user) {
        this.user = user;
    }

    @Override
    public List<Question> search(List<Question> questions) {
        return questions.stream().filter(question -> question.getCreator().equals(user)).toList();
    }
}
