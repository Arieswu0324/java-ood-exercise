package lrucache;

import ATMSystem.entity.Account;
import lrucache.entity.LRUCache;
import stackoverflow.entity.User;

public class LRUCacheMain {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(5);
        User user = new User("张三", "123@email.com");
        cache.put("user" + user.getName(), user);
        Account account = new Account("张三");
        cache.put("account" + account.getUserName(), account);

        User user2 = new User("李四", "234@email.com");
        cache.put("user" + user2.getName(), user2);
        Account account2 = new Account("李四");
        cache.put("account" + account2.getUserName(), account2);


        User user3 = new User("王五", "345@email.com");
        cache.put("user" + user3.getName(), user3);
        Account account3 = new Account("王五");
        cache.put("account" + account3.getUserName(), account3);

        User user4 = (User) cache.get("user" + user3.getName());
        System.out.print(user4 == null);

    }
}
