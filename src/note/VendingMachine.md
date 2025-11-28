#  这篇主要是ACID思想的练习

### 1. 什么时候用锁，什么时候用并发安全的数据结构
锁的并发控制力度是大于数据结构的，所以如果逻辑只涉及数据结构的并发修改，可以直接用并发安全的数据结构。
但如果逻辑复杂，并发安全的数据结构是无法照顾到操作与操作之间的“原子性”的，此时需要锁控制，将需要保证原子性的逻辑放在一起。

### 2. CopyOnWriteList/Set的开销
CopyOnWriteArrayList 的滥用： stocking 使用了 CopyOnWriteArrayList。这个集合的特点是：每次修改（add/remove）都会复制整个底层数组。
如果该商品库存很大，每次购买都会触发一次数组复制，造成大量的内存开销和 GC 压力。

### 3. Transaction的设计
这个实现中，dispense接口涉及查货，找零，这两个应该放到一起是一个事务。
最开始的实现是收钱-查货-如果货不够-退钱-如果够-取出后-进行找零-找零的过程中会对fund同时进行修改-如果最后发现找不开-退钱-fund状态回退
这种设计就比较复杂，设计上可以先基于当前快照进行计算-最后统一提交修改任务，这样可以防止复杂的分步回退。
检查 - 执行 (Check-Then-Act) + 锁

### 4. 安全性
getAvailableProducts 泄露内部状态： 直接返回了 stocking 对象。外部调用者可以拿到这个 Map 并随意 clear() 或修改里面的 List，破坏封装性。

应该返回深拷贝副本。 注意如果是immutableCollection只有外皮是安全的，machine.getAvailableProducts().get("Coke").clear(); 依然可以一键清库存。
并且需要在 lock.lock() 保护下进行拷贝，以保证生成的快照是**时间点一致（Consistent Snapshot）**的。如果不加锁， 可能出现“复制了一半，另一个线程修改了数据”的情况

### 5. 快照
对于这种有多线程操作的状态时，对于依据于某个状态进行计算时，都建议使用快照思想。
计算找零时，使用临时的一个资金快照来模拟计算Map<Money, Integer> tempFunds = new HashMap<>(this.funds);

### 6. computeIfPresent
computeIfPresent 的机制是：如果 Lambda 返回 null，Map 会自动移除该 Key。在 Lambda 内部手动操作 Map 结构通常是非法的或未定义的行为
```      funds.computeIfPresent(entry.getKey(), (k, v) -> {
                v = v - entry.getValue();
                if (v == 0) {
                    funds.remove(entry.getKey());//如果最后一个面值的钞票被取出，从fund里移除，这里不是好习惯
                    //不应该在compute里效用remove
                    //如果 Lambda 返回 null，Map 会自动移除该 Key
                }
            });
            
```

### 7. 深拷贝
- 不可变对象Enum, Integer, etc - 对于这种深拷贝，只拷贝容器即可。

   
   Integer (以及所有包装类 Long, String 等)， 看源码，它们的值都是final 修饰
   没有“修改”，只有“替换”： 当你写 i = i + 1 时，并没有修改原本那个 Integer 对象里面的 value。而是计算出了一个新的值，创建了一个全新的 Integer 对象，然后把你的变量引用指向了这个新对象。旧的对象如果没人用了，会被垃圾回收（GC）
   枚举比较特殊，它既是类，又是单例模式的集合，引用不可变（JVM 保证）： 当你定义 enum Money { TEN, FIVE } 时，JVM 保证全局只有唯一的一个 Money.TEN 实例对象。你无法 new Money()，也无法销毁它。因此，作为 Map 的 Key 时，它永远是同一个对象的引用

- 对于可变对象 - 深拷贝要每一层都拷贝

### 8. 如何判断对象是否是不可变对象
-  类由 final 修饰 (The Class is Final)
- 所有字段都是 private 和 final (Private and Final Fields)
- 没有setter
- 对引用类型字段进行“防御性拷贝” (Defensive Copies)，如果当前类中持有其他引用，那么是final也没用
```public final class Period {
   private final Date end; // Date 是可变的！

   public Period(Date end) {
   this.end = end; // ❌ 危险：直接引用了外部传入的对象
   }

   public Date getEnd() {
   return end; // ❌ 危险：把内部对象的引用泄露给了外部
   }
   }
   ```
// 攻击方式：
```Date d = new Date();
Period p = new Period(d);
d.setYear(100); // 外部修改 d，p 内部的 end 也跟着变了！p 就不再是不可变的了。
```

真正的不可变：
```public final class Period {
private final Date end;

    public Period(Date end) {
        // ✅ 构造时：创建一个新副本
        this.end = new Date(end.getTime());
    }

    public Date getEnd() {
        // ✅ 获取时：返回一个新副本
        return new Date(end.getTime());
    }
}
```

- 构造期间不要让 this 逸出 (No 'this' Escaping)
   目的： 确保对象在完全构造好之前，外界看不到它。如果在构造函数里就把 this 传给了其他线程，其他线程可能会看到初始化了一半的字段（比如还是 null 或 0）。

### 9. record class
ProductStock是一个DTO，ProductStock 是一个典型的 “不可变数据载体” (Immutable Data Carrier)。注意Record 生成的访问器方法不带 get 前缀。

Record 的设计初衷：是作为不可变数据的透明载体（Transparent Carrier）。它的隐含逻辑是：“数据已经在外面准备好了，我只是负责把它们打包传运。”
因此，Record 的标准构造函数（Canonical Constructor）要求传入所有字段，不适合Entity 这些带有业务逻辑的类。

record是JDK 的一个新特性，它减少了样板代码，在早期的JDK版本中，可以用private static class 且成员都是final去定义一个不可变类
对于private record，static可以省略

### 10. State Pattern
状态机实体类对于不同的状态有不同的行为时，可以设计为这个实现模式。此时需要有状态接口，不同的具体状态接口实现不同状态下的行为。
这些接口是被主类委托的，来改变主类状态，所以参数有this。


### 11. 快照回滚- Memento Pattern
内存中对当前对象状态的存储，可以用于撤销。
组成：
- Originator: 会改变状态的类
- Memento: 保存快照的实体类，一般被设计为不可变类
- Caretaker：保存快照的类，如果当前保存的不只是一个Memento对象，而是History，那么可以设计这样一个类去封装不同的快照版本
在VendingMachine的设计中，dispense方法的事务性操作是：扣除库存，收钱，找零。涉及两个状态的改变：商品，钱数。如果在这个过程中被中断，需要回滚状态。
这个场景中，Originator是VendingMachine类，Memento的快照需要存储的是所涉及到的状态改变的所有属性。在这个例子中没有Caretaker，因为快照不需要存储。

### 12. 接口默认方法
接口中可以有带实现的default方法，实体类可以override，设计意图是减少代码重复。它可以被override，也可以不被override


### 13. Code Smell—构造函数
构造方法中一般不调用加锁的方法，感觉很奇怪，可以直接写代码逻辑，而不追求完全的复用。

### 14. Code Smell-访问限定
protected的定义是允许子类访问，它的访问粒度是大于package-private的。 默认不加任何访问限定符，是package-private，即同包访问。
限定符,关键字,同一类中,同一包中,不同包的子类中,不同包的非子类中 (World),封装级别

private,private,✅,❌,❌,❌,最严格 (仅限类内部)
package-private,无关键字 (默认),✅,✅,❌,❌,包级私有 (仅限同一包)
protected,protected,✅,✅,✅,❌,受保护 (同一包或子类)
public,public,✅,✅,✅,✅,最宽松 (全局可访问)