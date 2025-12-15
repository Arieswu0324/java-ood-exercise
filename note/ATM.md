### 1. compute, computeIfPresent, computeIfAbsent
- computeIfAbsent
  第二个参数为Function，单参lambda 表达式，触发条件是key不存在，参数是key
  场景是：新增，懒加载：查不到就创建一个
- computeIfPresent
  第二个参数是BiFunction，参数是(k,v)的lambda表达式，表示已有mapping，但需要覆盖，覆盖逻辑是BiFunction
  lambda 表达式返回null 表示删除key，不为null表示更新
  场景是：更新现有数据
- compute
  无论存不存在都执行，双参lambda 表达式，参数是k, v
  lambda 表达式返回null 表示删除key，不为null表示更新
  场景是：不管原来有没有，都要改

### 2. AtomicReference与锁
AtomicReference底层是无锁实现（CAS，乐观锁，非公平锁）。

### 3. Transaction相关逻辑的设计-Check-And-Act
涉及余额，扣除这种交易时 先检查（预扣），再执行，可以fail fast，如果检查失败，没有真正的执行不需要回滚。
否则直接执行了，是一种runtime fail，失败的时候要区分业务失败（余额不足）和系统失败，比较麻烦。
但是这两步操作，很容易引起race condition，毕竟check和act 之间是有空间的。如果是本地JVM操作一般要加锁

### 4. synchronized 关键字与ReentrantLock
这俩都是悲观锁，都是线程来了的时候先进行独占访问约定。即被观认定一定有线程来抢，所以先上锁。
CAS是乐观锁是因为它没有上锁，而是自旋抢占，假定没有线程来抢。
synchronized 关键字是非公平锁，它没有严格实现FIFO，虽然也有一个队列维护等待线程，但是新来的线程是和对头一起竞争的，不是绝对的公平。
ReentrantLock里面的AQS维护的CLH队列是FIFO的，是公平锁，不过也可以设为非公平实现。

### 5. 避免在try块中rollback
不是好的代码习惯，失败或异常统一在catch或finally中回滚

### 6. CAS操作
乐观锁，或者说无锁操作，通常来说效率更高。不涉及内核切换和唤醒阻塞线程的操作，但是占CPU，因为CPU在高并发场景下空转时间长。
根据场景，一般来说单纯的计数器适合CAS操作，复杂的业务或者线程数>4(内核数量)时还是用锁。
AtomicReference提供了CAS操作。

### 7. 分布式环境下的事务性
假定BankService涉及远程IO。在分布式环境下（本地内存 + 远程网络），加锁（Java Lock/synchronized）无法保证事务性。
Java 的锁只能控制“并发”，不能控制“分布式一致性”。锁是JVM级别的，不管跨机器的操作。
事务性”必须通过“加锁”来实现，这是单体数据库（如 MySQL 事务）的思维定式，因此deposit和withdraw 方法最开始用大锁保证事务性是一种错误。
改进后使用率 一种经典的分布式事务模式，它不依赖“锁”来强行绑定两个操作，而是依赖执行顺序和补偿逻辑。


