package stackoverflow.indexes;

import stackoverflow.entity.Question;
import stackoverflow.entity.Tag;

import java.util.concurrent.CopyOnWriteArrayList;

public class TagIndexSearch extends IndexSearch<Tag> {

    @Override
    public void addToIndex(Question q) {
        q.getTags().forEach(tag -> {
            indexMap.computeIfAbsent(tag, k -> new CopyOnWriteArrayList<>()).add(q);
        });
    }
}
