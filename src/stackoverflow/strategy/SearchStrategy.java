package stackoverflow.strategy;

import stackoverflow.entity.Question;

import java.util.List;

public interface SearchStrategy {
    List<Question> search(List<Question> questions);
}
