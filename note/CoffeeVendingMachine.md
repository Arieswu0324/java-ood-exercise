### 1. volatile 变量什么时候需要保证原子性
又是这个问题，还是没有学透。在本例中，CoffeeMachine类中，strategy被认为是一个volatile对象，即如果发生修改，应保证对其他线程立即可见。
dispense方法因为整体业务的原子性加了锁，所以strategy在被调用时是锁保护的。
在getter和setter方法时，我起初并没有加锁保护，因为我觉得不必要，JVM保证引用的赋值是原子性的。
但这里有个问题的，就是如果同时有两个线程，一个在setStrategy，一个在使用strategy.find，那么会怎么样呢？

首先要明确，在JVM层面，strategy.find是翻译成两个命令，首先读到strategy的地址值，然后根据这个地址里的内容去执行find代码。
而代码执行的过程在JVM栈帧上，是线程私有的，只要走到find逻辑里，就不用考虑线程安全问题。

相对于并发模型而言：代码的“执行”是逻辑行为，“执行”本身是不需要锁的，只有“执行过程中访问共享数据”才需要锁。
还是那个问题，锁本质锁的是共享内存，而不是代码逻辑。

回到这个案例，问了G老师和C老师，从内存角度上，getter和setter可以不加锁。从业务角度上，就要特殊考虑是否需要业务数据的一致性。
比如如果我在执行idleState.dispense的同时，另一个线程调用了maintenance，这个时候代码逻辑还会继续进行dispense，
如果调用getter，会拿到maintenance，但其实dispense在执行idleState未完成的内容。
此时就看业务要求了，如果严格要求读写一致，那么getter和setter也需要上锁。

这里还有一个细节，为什么一个如果加锁，getter, setter, strategy.find业务逻辑要上同一把锁？
同一把锁的意义是内存可见性+隔离性，即同一个共享内存对象持有锁时，其他线程是不能进入的(读写都不行)。
且一个线程释放 锁L 之前的写入，要对随后 获取 同一个锁 L 的另一个线程可见。
如果用不同的锁，互斥性会失效。

### 2. Happens-Before规则
1. 程序次序规则 (Program Order Rule)
   含义：在一个线程内，代码按照书写的顺序执行。前面的操作 Happens-Before 后面的操作。
2. 管程锁定规则 (Monitor Lock Rule) —— 解释了你的“同一把锁”问题
   含义：一个 unlock 操作 Happens-Before 后续对同一个锁的 lock 操作。
解释： 线程 A 解锁（Unlock）之前的所有修改，对于随后获取（Lock）这把锁的线程 B 都是可见的。
注意：必须是同一个锁！如果是不同的锁，这条规则不生效，也就没有可见性保证。

3. volatile 变量规则 (Volatile Variable Rule)
   含义：对一个 volatile 变量的写操作，Happens-Before 后续对这个变量的读操作。
解释： 线程 A 写了 volatile 变量 x。 线程 B 读了 volatile 变量 x。 线程 B 一定能看到 A 写的值。

4. 传递性 (Transitivity) —— 这是最“神奇”的一条
含义：如果 A HB B，且 B HB C，那么 A HB C。 
应用（搭便车效应）：这是 volatile 和锁能保护非线程安全变量的基础。