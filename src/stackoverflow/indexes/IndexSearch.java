package stackoverflow.indexes;

import stackoverflow.entity.Question;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class IndexSearch<K> {
    protected final Map<K, List<Question>> indexMap;

    IndexSearch() {
        this.indexMap = new ConcurrentHashMap<>();
    }

    public abstract void addToIndex(Question q);

    public List<Question> getByIndex(K key) {
        return indexMap.get(key);
    }

}
