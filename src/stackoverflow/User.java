package stackoverflow;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class User {
    private final String id;
    private final String name;
    private final String email;
    private AtomicInteger score;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        score = new AtomicInteger(0);
        id = UUID.randomUUID().toString();
    }

    public void updateScore(int increment) {
        score.addAndGet(increment);
    }

    public String getId() {
        return id;
    }

    public Integer getScore() {
        return score.get();
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    //equals/hashCode 只应基于不可变字段
    @Override
    public int hashCode() {
        //return Objects.hash(name, email, score.get());
        return Objects.hash(name, email);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        User other = (User) obj;

        if (!Objects.equals(other.name, name)) {
            return false;
        }

        if (!Objects.equals(other.email, email)) {
            return false;
        }

        return score.get() == other.score.get();
    }
}
