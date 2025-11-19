package stackoverflow.strategy;

import stackoverflow.entity.Question;
import stackoverflow.entity.Tag;

import java.util.List;

public class TagSearchStrategy implements SearchStrategy {

    private final Tag tag;

    public TagSearchStrategy(Tag tag) {
        this.tag = tag;
    }

    @Override
    public List<Question> search(List<Question> questions) {
        return questions.stream().filter(question ->
                question.getTags().stream().anyMatch(tag::equals)
        ).toList();
    }
}
