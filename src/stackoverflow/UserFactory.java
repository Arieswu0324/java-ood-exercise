package stackoverflow;

public class UserFactory {
    public static User create(String name, String email) {
        return new User(name, email);
    }
}
