package stackoverflow;

import java.util.List;

public class TagSearchStrategy implements SearchStrategy {

    private final Tag tag;

    TagSearchStrategy(Tag tag) {
        this.tag = tag;
    }

    @Override
    public List<Question> search(List<Question> questions) {
        return questions.stream().filter(question ->
                question.getTags().stream().anyMatch(tag::equals)
        ).toList();
    }
}
