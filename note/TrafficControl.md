### 1. Entity 识别

这个OOD的题目出的不是很好，我在实现的时候也想复杂了，看给出的实现发现：红绿灯的时长duration这个属性，
相对来说是固定的，而不是动态的，相当于红绿灯就是固定交替的，而不是每一次闪烁的时长都不同。这一点很重要，
因为如果是固定的，那么红绿灯的duration就是intersection的属性，而如果每一次闪烁的时长不同，那么duration就是灯信号的属性。
如果面试遇到这个问题，感觉陷入了自己的思维漩涡，那么就解不开这个题了，还是需要及时沟通，明确需求很重要。

### 2. ExecutorService资源的关闭

```
executor.shutdown();
try {
if (executor.awaitTermination(15, TimeUnit.SECONDS)) {
executor.shutdownNow();
}
} catch (InterruptedException e) {
executor.shutdownNow();
    Thread.currentThread().interrupt();
}
```

### 3. ExecutorService 与ThreadPoolExecutor
ExecutorService是接口，如果定义的线程池是变量，建议用ExecutorService 类型做声明。
```
ExecutorService executor = new ThreadPoolExecutor( ... );
```
使用结束都需要对声明周期进行管理，显式调用shutdown方法关闭资源


### 4. 流程控制
系统的关闭有两种方案，shutdown标识在TrafficControlSystem中，每次提交任务之前判断；
或放在Runnable的实现中， 判断标识再执行内容。后者封装性更好，每一个任务都可以解耦。

### 5. record类
record 类中可以override getter 方法，见SignalPhase示例

### 6. volatile修饰的变量加不加锁的问题
volatile 只保证可见性和有序性，不保证复合操作的原子性，但如果只有一个线程写，不需要原子性保证。
CC建议我Intersection类中的IntersectionState变量加volatile，此时state.getNext方法会对state本身进行读写，
我疑问此时是否应该对读写操作加锁。在这个场景下是不需要的，因为对state的读写，只有一个线程，不存在多线程同时写的情况。
而此时为什么要引入volatile呢？作用是让其他线程看到最新值（其实在当前实现里用不到，如果有setter方法，加上volatile就保证可见性了）
getNext方法只是创建了新对象并返回，没有修改共享状态，不需要同步。而且JVM保证了引用赋值本身是原子性的，就是state = state.getNext这里。
复合赋值(i++) 这种是飞原子的，所以会有AtomicInteger这种类包装了一些原子操作在里面。

如果对象是多线程写操作，那么要加锁保证原子性