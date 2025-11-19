package stackoverflow.indexes;

import stackoverflow.entity.Question;
import stackoverflow.entity.User;

import java.util.concurrent.CopyOnWriteArrayList;

public class UserIndexSearch extends IndexSearch<User> {

    @Override
    public void addToIndex(Question q) {
        indexMap.computeIfAbsent(q.getCreator(), k -> new CopyOnWriteArrayList<>()).add(q);
    }
}
