### 1. volatile
volatile只保证引用的可见性，如果引用是集合，不保证内部操作的原子性。因此
```private volatile List<OutputDestination> destinations;``` 
此时返回destinations里面的OutputDestination 对象是可以修改的，那么在它被迭代时，可能被其他线程修改。
抛ConcurrentModificationException

### 2. 循环依赖
当前在开发LoggingFramework，客户端的接口是Logger提供的，AsyncOutputProcessor是“内部”的，如果在里面
```private static final Logger logger = LoggerFactory.getLogger(AsyncOutputProcessor.class);```
就陷入循环依赖的问题了。不应该使用Logger 本身记录内部日志


### 3. CopyOnWrite集合
this.destinations.clear();  // CopyOnWriteArrayList的clear会拷贝整个数组

### 4. Shutdown 时序问题
Main 方法中的代码：
```
executor.shutdown();
manager.shutdown();
```
executor.shutdown() 只是发起关闭，不会等待已提交的任务完成。然后立即调用了manager.shutdown()，这个方法内部会关闭AsyncOutputProcessor的executor，
但是Main 方法中的executor任务还没有执行完，在调用时尝试提交到AsyncOutputProcessor会提交失败，抛出拒绝异常。
说白了就是manager执行关闭的时候，上一个executor里的人物还没执行完，想继续调用manager里的线程池，但会提交失败
