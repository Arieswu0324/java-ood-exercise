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