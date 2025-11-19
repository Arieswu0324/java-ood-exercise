package stackoverflow;

import stackoverflow.entity.*;
import stackoverflow.enums.VoteType;
import stackoverflow.indexes.IndexSearch;
import stackoverflow.indexes.TagIndexSearch;
import stackoverflow.indexes.UserIndexSearch;
import stackoverflow.strategy.KeywordSearchStrategy;
import stackoverflow.strategy.SearchStrategy;
import stackoverflow.strategy.TagSearchStrategy;
import stackoverflow.strategy.UserSearchStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        StackOverflowSystem instance = StackOverflowSystem.getInstance();

        UserIndexSearch userIndexSearch = new UserIndexSearch();
        TagIndexSearch tagIndexSearch = new TagIndexSearch();

        List<IndexSearch<?>> indexes = new ArrayList<>();
        indexes.add(userIndexSearch);
        indexes.add(tagIndexSearch);
        instance.addIndexSearches(indexes);



        User user = instance.createUser("user", "abc@email.com");

        User user1 = instance.createUser("user1", "def@email.com");

        User user2 = instance.createUser("user2", "ghi@email.com");

        Question question = instance.createQuestion("Title", "this is my content", user, Set.of(new Tag("TEST")));

        Comment comment = instance.addComment(question, "this is my question comment", user1);

        Answer answer = instance.answerQuestion(question, "this is my answer", user1);
        Comment comment1 = instance.addComment(answer, "this is another comment", user2);

        instance.vote(question, user2, VoteType.VOTE_UP);
        instance.vote(answer, user2, VoteType.VOTE_DOWN);

        instance.acceptAnswer(user, answer, question);


        System.out.println("---打印搜索结果---");
        UserSearchStrategy userSearchStrategy = new UserSearchStrategy(user);
        TagSearchStrategy tagSearchStrategy = new TagSearchStrategy(new Tag("TEST"));
        KeywordSearchStrategy keywordSearchStrategy = new KeywordSearchStrategy("content");
        List<SearchStrategy> strategies = new ArrayList<>();
        strategies.add(userSearchStrategy);
        strategies.add(tagSearchStrategy);
        strategies.add(keywordSearchStrategy);
        List<Question> search = instance.search(strategies);
        search.forEach(q -> {
            System.out.println(q.getTitle());
        });


        List<Question> byIndex = userIndexSearch.getByIndex(user);
        byIndex.forEach(q->{
            System.out.println(q.getTitle());
        });

        System.out.println("---打印积分---");
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
