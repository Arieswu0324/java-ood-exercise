# Entity

## ParkingLotSystem

- ParkingFloor 每层
- RateMap 计费
- ParkingStrategy 分配策略
- tickets 已经停的车辆情况
- 主要实现：park/unpark 方法

## ParkingFloor

- ParkingSpot

## ParkingSpot

- Size
- Ticket 占用关联的 ticket
- isOccupy 是否占用

## ParkingTicket

- ParkingSpot 停在哪个车位
- startTs/endTs 时间
- rate 费率
- fee 费用

## Vehicle

- Car
- Motor
- Truck

---

# 实现注意到的问题

## 设计模式

### 1. 工厂模式

这里使用 Factory Method Pattern，一般流程是找到抽象类，分别实现子类。定义抽象工厂，分别实现子工厂负责生产子类。作用是把 new
方法包装到工厂方法里。

工厂模式的代码量主要增加在工厂这些 entity 上，毕竟是把原有类又包了一层。

### 2. 单例模式

在使用工厂模式时，一般工厂本身就是单例的。单例要考虑线程安全。有两种实现方式：

- **Eager Initialization**：当加载时，单例已经创建。后续的调用都只调用这个单例，天然线程安全。占用内存，因为如果工厂一直不被调用，但对象会一直存在。
  这里会联想到 Spring IoC 容器在创建 Bean 的时候默认用的就是 Eager Initialization。当然，如果用 `@Lazy`
  注解则启动懒加载，这个方法可以用于修复循环依赖的问题。

- **Lazy Initialization**：当单例方法被调用时才加载。此时要考虑线程安全，实现是 DCL。注意此时单例是 `volatile`
  修饰的保证对象在不同线程间的可见性，同时阻止指令重排序。

### 3. 观察者模式

需求：Could notify customers about available spots.
观察者模式，本质是一个一对多的消息通知模式。明白这个模式，但在自己写代码的时候发现无从下手，是因为我在系统学习观察者模式前，思维已经被Kafka的publisher-subscriber模型固化了。
总觉得在消息发布和消费之间应该有个中间队列，让我一开始的思维陷入死胡同，想得很复杂。
实际上解决观察者模式，可以从核心思路开始：明确事件（被观察者），接收方（观察者）。事件触发条件。
于是按着这个思路，一个简单的PUSH模型：

1. 定义事件Entity
2. 定义观察者（抽象），具体收到事件的行为onEvent()
3. 在产生事件的类中，持有被观察者集合，即观察者可以订阅的主题。实现subscribe/unsubscribe方法，实现notifyAll()方法,
   notifyAll内部是分别调用观察者的onEvent()
4. 明确事件触发条件： 当unpark 被调用时，产生一个available spot事件-事件触发，调用notifyAll()方法

在这种实现中，主要的Entity是
Topic/Observable (observers, subscribe, unsubscribe, notifyAll) 在本例中主题和生产者是同一个对象，
Observer(onEvent - 根据发送的事件有具体的行为)

而对于PULL模型来说，事件是没有传给被观察者的。而且在具体的onEvent方法中，Observer要去Observable中调用参数获得想要的信息。
PULL模型的优势是Observer可以主动的获取自己想要的信息。

PULL vs PUSH
PULL
被观察者只发通知，观察者主动拉取需要的数据。

PUSH:
- ✅ 观察者无需了解被观察者的内部结构
- ✅ 实现简单，逻辑清晰
- ✅ 事件数据封装在 Event 对象中，扩展性好

- ❌ 如果事件数据很大，可能造成内存浪费
- ❌ 观察者可能不需要所有数据，但还是被推送了

**学习这观察者模式时，很容易联想到kafka 消息队列，但是这两个不完全一样，后者是Pub/Sub模式。
在比较复杂的Kafka消息队列中，多了一个中间件，把生产者和主题隔离了，达到削峰填谷的作用。对于生产者来说，是PUSH 模型，生产产者把消息推送到所有subscribed broker 中，此时broker是观察者。(可以这么类比，但不完全准确)。
而对于消费者来说，消费者是POLL模型，此时主题是observable，consumer是观察者，观察者是定期去主题中请求消息进行处理的。这与观察者模式的PULL模型不同，观察者模式中，事件发生时，被观测主题还是会notifyALL，而kafka broker则不会notify**


---

## Java 多线程

### 4. 线程安全问题

对于这个 OOD，很自然会联想到多线程。但在实现的过程中发现思路并不清楚。面对多线程的场景，我觉得思路一般是：识别哪些对象会被多线程访问，进而采用什么方法对线程进行隔离。

之前一直在犯一个错误，在看到多线程问题时，总是想到对方法上锁。本质是没有搞清楚，线程安全，其实是对于共享内存上的对象而言的，"
上锁"本质上是对对象上锁，并不是对方法而言的。

这里要提到 JVM 的基础知识。方法调用发生在自己的栈帧上，方法内部的变量都是本地变量，虽然本地对象也是存在在堆上，但是它们是被自己栈帧独立访问的，栈弹出时销毁。它们本身就是线程隔离的。

而类变量不一样，它们是线程共享的，不同的方法、调用可能同时对堆内存上的对象进行修改，这时就会有线程安全和竞速问题。

### 5. 本题中被多线程访问的对象识别

一个好的习惯是"从小到大"，从细粒度到粗粒度

- **ParkingSpot**：场景：分配车位和抢车位。多线程可能同时执行分配任务得到了相同的车位，但是只有一个可以抢成功

- **ParkingFloor**：场景：分配车位和剩余车位。多线程在分配车位时会同时影响剩余车位数量，和每层车位的占用情况

- **tickets**：场景：车辆进出车库时，多线程对停车情况进行更新

  注意：ParkingLotSystem 中的 floor、rateMap、strategy 变量简单来讲应该是不可变类型的，即在创建对象时就固定。这里不是
  final，final 指的是对象引用不变，但对象内容还是可变的。

  要理解不可变和 final 的区别。不能一个线程正停车呢，另一个线程直接 `floor.empty()` 了，所以这些变量应该是不可变的
  Immutable Collection。如果这些值需要改，那么应该是 copy 进行替换（浅拷贝，复制引用为新的）。

### 6. 线程隔离的方法

- **synchronized/ReentrantLock**
  ：悲观锁，线程认为对当前对象的访问一定会有竞争，因此为保证独占，我上来先加锁，处理完逻辑再解锁。其他线程会阻塞，挂起，唤醒，线程切换开销大，比较"
  重"。

- **CAS**：乐观"锁" 严格来说 CAS 应该不算锁，主要是自旋抢占。对于竞争是非阻塞的，其他线程会返回抢占失败。CPU
  指令级别的操作，非常快，不需要操作系统介入，比较"轻"。
  `AtomicBoolean`, `ConcurrentHashMap` 底层都是 CAS 保护的

### 7. 线程安全思路

这里是有顺序的：

a) 使用封装好的线程安全的类

b) 原子变量与 CAS

c) 显式加锁

---

## 代码细节

### 8. Stream API、lambda 表达式

stream API 用着顺手，但很容易隐藏线程安全问题。

```java
// 这里线程不安全，当 stream 返回 spot 时，它可能被其他线程占用了
return Optional.ofNullable(spots).flatMap(list ->list.stream().filter(spot ->!spot.isOccupied()).
findFirst());
```

于是很容易想到改写成以下，但这又违反了函数式编程的最佳实践，lambda 表达式中应该是无状态修改的纯函数，（只读，不修改），否则可读性差，bug
不好找

```java
return Optional.ofNullable(spots).flatMap(list ->list.stream().filter(ParkingSpot::occupy).findFirst());
```

这个场景最好用传统 for，而不是 stream API，即代码中保留的实现方式

### 9. Map 的原子操作

对于普通 map（如 HashMap）来说，它的 put、get 等操作不是线程安全的。

对于线程安全的 map（如 Concurrent）来说，它的 put、get 是线程安全的原子操作。但如果组合，那就不是线程安全的了

```java
availableCounts.put(spot.getSize(),availableCounts.

get(spot.getSize())-1);
```

对于这种情况要用 compute 相关 API，它们是原子的，受 CAS + synchronized 保护。

### 10. ConcurrentHashMao中的lambda表达

注意 ConcurrentHashMap compute 中的 lambda 表达式是受 synchronized 保护的，但是这个锁保护是有作用域的。
以下代码有个隐秘的BUG, 用了非线程安全的HashSet，应该改为k-> ConcurrentHashMap.newKeySet()
这里有个误区，lambda表达式受CAS保护，但如果这里创建的是一个HashSet，那么后面的set.add则是线程不安全的

```java
observer.getInterestedSpot().forEach(size ->observers.computeIfAbsent(size, k ->new HashSet<>()).
add(observer));
```

### 11. Optional 的用法

设计可返回空值的方法时，作为返回值，封装一个返回对象，这样对于这个对象的调用法，可以进行非空判断，避免 unexpected NPE。

### 12. MessageFormat 的用法

MessageFormat 在对信息格式化的时候，是严格按照 index 填充的，因此格式为：

```java
String info = MessageFormat.format("剩余车位数：小型 {0} 个， 中型 {1} 个， 大型 {2} 个",
        availableCounts.getOrDefault(SpotSize.SMALL, 0),
        availableCounts.getOrDefault(SpotSize.MEDIUM, 0),
        availableCounts.getOrDefault(SpotSize.LARGE, 0));
```

同时这里的填充值如果为空则会报 NPE，所以 map 要用 `getOrDefault` 赋默认值

### 13. AtomicReference

`AtomicReference<V>` 允许原子性地读取和写入一个引用类型的变量，内部实现是 CAS 的非阻塞。也就是说保证 get/put 是原子性的，线程安全。

同时，`private volatile V value;` 保证其内部对象的可见性。

###  <T extends ParkingLotObserver> 泛型
当同时实现pull observer和push observer的时候，发现subscribe/unsubscribe方法逻辑重复，用泛型做抽象是个不错的选择。
```java
private <T extends ParkingLotObserver> void addToObserverMap(Map<SpotSize, Set<T>> observerMap, T observer) {
observer.getInterestedSpot()
.forEach(size -> observerMap.computeIfAbsent(size, k -> ConcurrentHashMap.newKeySet()).add(observer));
}
```

        
        
    

   


