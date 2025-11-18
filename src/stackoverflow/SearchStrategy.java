package stackoverflow;

import java.util.List;

public interface SearchStrategy {
    List<Question> search(List<Question> questions);
}
