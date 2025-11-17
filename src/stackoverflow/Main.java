package stackoverflow;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        StackOverflowSystem instance = StackOverflowSystem.getInstance();

        User user = UserFactory.create("user", "abc@email.com");

        User user1 = UserFactory.create("user1", "def@email.com");

        User user2 = UserFactory.create("user2", "ghi@email.com");

        Question question = instance.createQuestion("Title", "this is my content", user, Set.of(new Tag("TEST")));

        Comment comment = instance.addComment(question, "this is my question comment", user1);

        Answer answer = instance.answerQuestion(question, "this is my answer", user1);
        Comment comment1 = instance.addComment(answer, "this is another comment", user2);

        instance.vote(question, user2);
        instance.vote(answer, user2);

        Optional<List<Question>> search = instance.searchByTag(Set.of(new Tag("TEST")));

        search.ifPresent(questions -> questions.forEach(q -> System.out.println(q.getTitle())));
        search = instance.searchByUser(user);
        search.ifPresent(questions -> questions.forEach(q -> System.out.println(q.getTitle())));
        search = instance.searchByUser(user1);
        search.ifPresent(questions -> questions.forEach(q -> System.out.println(q.getTitle())));

        System.out.println(user.getName() + "积分为" + user.getScore());
        System.out.println(user1.getName() + "积分为" + user1.getScore());
        System.out.println(user2.getName() + "积分为" + user2.getScore());

        System.out.println("---打印问题内容---");
        System.out.println("问题：" + question.getContent());
        System.out.println("创建时间：" + question.getCreateTs() + "， 创建人：" + question.getCreator().getName());
        System.out.println("---问题评论---");
        question.getComments().forEach(c -> System.out.println(c.getContent()));
        System.out.println("---回答---");
        question.getAnswers().forEach(an -> System.out.println(an.getContent()));
        System.out.println("---回答评论---");
        question.getAnswers().forEach(an -> an.getComments().forEach(c -> System.out.println(c.getContent())));


    }
}
