package stackoverflow;

import java.util.List;

public class KeywordSearchStrategy implements SearchStrategy {
    private final String keyword;

    KeywordSearchStrategy(String keyword) {
        this.keyword = keyword;
    }


    @Override
    public List<Question> search(List<Question> questions) {
        return questions.stream().filter(q -> q.getContent().contains(keyword) || q.getTitle().contains(keyword
        )).toList();
    }
}
