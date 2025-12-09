### 1. record类入参
User user = new User(101L, null);入参允许为null


### 2. 线程级别的定时任务
```java
private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
```
然后在构造方法或者@PostConstruct方法中提交任务

### 3. hashcode和equals方法
相等的对象必须有相同的hashCode，所以重写了equals方法，必然要重写hashcode方法

### 4. 泛型
对于TaskSearchStrategy 接口，实现类的定义public class PrioritySearchStrategy<Priority> implements TaskSearchStrategy<Priority>这样是有问题的。
实现类的<Priority>涉及到泛型遮蔽，Generics Naming Shadowing。仔细看这时当前类没有import Priority类，因为在此时意味着UserSearchStrategy 类本身时声明的一个新的、未受约束的类型变量。
就是跟<K>一个道理。它的存在会遮蔽真正的<Priority>类型指定，就是后面那个。
```java
public class UserSearchStrategy implements TaskSearchStrategy<User> {}
```
如果要对泛型进行约束，则应该是这样
```java
public class AssigneeSearchStrategy<K extends User> implements TaskSearchStrategy<K> {}
```

### 5. 开闭原则
search方法不应该直接传递taskMap引用，违反了开闭原则，策略对象可以修改内部数据，不安全。一般有以下两种方案：
Collections.unmodifiableMap(taskMap) vs new HashMap<>(taskMap)。
前者方法是创建原 Map 的一个只读视图（View），后者是创建原 Map 的一个独立副本（Deep Copy of the structure），即浅拷贝。

### 6. ConcurrentHashMap的锁保护范围
并发Map的锁保护的是对Map 的操作，如果map 没有写操作，其实不用加锁。至于对Map 中的对象如果有写操作，则需要map自己的锁去保护。


### 7. @FunctionalInterface注解
@FunctionalInterface Java8引入，表示函数式接口，当前接口有且只有一个抽象方法，但是可以有多个default方法。
主要也是提醒和显式文档化的作用。策略模式的抽象接口可以用。

