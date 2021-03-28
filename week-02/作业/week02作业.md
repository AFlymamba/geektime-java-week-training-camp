## 题目一：分别使用 串行、并行、CMS、G1等不同 GC 策略 执行 GCLogAnalysis.class 文件，分析 GC 日志。

环境信息：

```plain
操作系统：Windows
硬件资源：2核4线程，12G内存
JDK：
java version "1.8.0_51"
Java(TM) SE Runtime Environment (build 1.8.0_51-b16)
Java HotSpot(TM) 64-Bit Server VM (build 25.51-b03, mixed mode)
测试代码：
  课件提供的 GCLogAnalysis.java
```
### JDK8默认GC策略（并行 ParallelGC）

#### 参数：设定初始堆大小（-Xms）和堆最大内存大小（-Xmx）为1g

java -Xmx1g -Xms1g-XX:+PrintGCDetails -XX:+PrintGCDateStamps

>-XX:+UseParallelGC 表示 JVM 对 年轻代 使用 Parallel Scavenge也就是并行复制标记算法，老年代采用的是 Serial Old 串行复制清除整理算法。
>并行GC：对年轻代采用的是标记复制算法，会触发 STW，对老年代采用的是标记清除整理算法，也会触发 STW，多线程（GC线程）处理垃圾。可以通过：-XX:ParallelGCThreads=n设定并行线程数。
>并行 GC：适合用于 多核服务器，看重吞吐量的系统，并行是为了充分利用系统资源
#### 日志信息

```plain
正在执行...
2021-03-28T16:02:36.345+0800: [GC (Allocation Failure) [PSYoungGen: 262144K->43519K(305664K)] 262144K->78674K(1005056K), 0.0312989 secs] [Times: user=0.00 sys=0.09, real=0.03 secs] 
2021-03-28T16:02:36.456+0800: [GC (Allocation Failure) [PSYoungGen: 305663K->43517K(305664K)] 340818K->149307K(1005056K), 0.0626987 secs] [Times: user=0.03 sys=0.08, real=0.06 secs] 
2021-03-28T16:02:36.625+0800: [GC (Allocation Failure) [PSYoungGen: 305661K->43509K(305664K)] 411451K->222278K(1005056K), 0.0543595 secs] [Times: user=0.06 sys=0.06, real=0.05 secs] 
2021-03-28T16:02:36.797+0800: [GC (Allocation Failure) [PSYoungGen: 305653K->43516K(305664K)] 484422K->290184K(1005056K), 0.0587741 secs] [Times: user=0.05 sys=0.03, real=0.06 secs] 
2021-03-28T16:02:36.974+0800: [GC (Allocation Failure) [PSYoungGen: 305660K->43514K(305664K)] 552328K->369341K(1005056K), 0.0527913 secs] [Times: user=0.03 sys=0.09, real=0.05 secs] 
2021-03-28T16:02:37.099+0800: [GC (Allocation Failure) [PSYoungGen: 305658K->43518K(160256K)] 631485K->440472K(859648K), 0.0387327 secs] [Times: user=0.03 sys=0.05, real=0.04 secs] 
执行结束，共生成对象次数：5982
Heap
 PSYoungGen      total 160256K, used 48284K [0x00000000eab00000, 0x0000000100000000, 0x0000000100000000)
  eden space 116736K, 4% used [0x00000000eab00000,0x00000000eafa76d8,0x00000000f1d00000)
  from space 43520K, 99% used [0x00000000fd580000,0x00000000fffffa58,0x0000000100000000)
  to   space 116224K, 0% used [0x00000000f1d00000,0x00000000f1d00000,0x00000000f8e80000)
 ParOldGen       total 699392K, used 396953K [0x00000000c0000000, 0x00000000eab00000, 0x00000000eab00000)
  object space 699392K, 56% used [0x00000000c0000000,0x00000000d83a65e0,0x00000000eab00000)
 Metaspace       used 3743K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 416K, capacity 428K, committed 512K, reserved 1048576K
```
#### 分析

* GC 过程
```plain
发生了 6 次 YoungGC
```
* 分析 第一次 YoungGC 日志
```plain
2021-03-28T16:02:36.345+0800: [GC (Allocation Failure) [PSYoungGen: 262144K->43519K(305664K)] 262144K->78674K(1005056K), 0.0312989 secs] [Times: user=0.00 sys=0.09, real=0.03 secs] 
在28号下午4点01触发，触发的区域在 年轻代，原因是：内存分配错误，说明此时 Young 区剩余的内存大小不足以存放新创建的对象，所以触发了 YoungGC，年轻代大小从 262M 被 回收到 43M，回收了 219M 空间，整个堆区使用情况和Young区一致，因为当前是第一次 YoungGC，Old区是空的，此时整个堆区使用大小自然和 Young区一致，整体堆大小从 262M 下降到 78M，说明堆整体空间回收了 184M，和 Young 区变化不同，说明存在 对象晋升 了。此次 YoungGC 花费了 31 毫秒时间。
```
* 堆区整体情况分析
```plain
Heap
 PSYoungGen      total 160256K, used 48284K [0x00000000eab00000, 0x0000000100000000, 0x0000000100000000)
  eden space 116736K, 4% used [0x00000000eab00000,0x00000000eafa76d8,0x00000000f1d00000)
  from space 43520K, 99% used [0x00000000fd580000,0x00000000fffffa58,0x0000000100000000)
  to   space 116224K, 0% used [0x00000000f1d00000,0x00000000f1d00000,0x00000000f8e80000)
 ParOldGen       total 699392K, used 396953K [0x00000000c0000000, 0x00000000eab00000, 0x00000000eab00000)
  object space 699392K, 56% used [0x00000000c0000000,0x00000000d83a65e0,0x00000000eab00000)
 Metaspace       used 3743K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 416K, capacity 428K, committed 512K, reserved 1048576K
分别说明了 Young 区，Old 区，元数据区，内存总量以及使用情况，这块存在一个问题，Young 区的 from 和 to 应该是相同大小才对，但是这次执行结果反馈并不一致，猜测可能是 JVM 的自适应导致的。
而且多次执行还发现 每次GC 日志不一定会完全一样，虽然是一致的代码，应该是当前系统进程间争抢资源等各种原因导致的。
```
* 调整一：修改并行 GC 的并行线程数
```plain
-XX:UseParallelGCThreads=n
有利有弊，这块需要参考当前默认线程数的。
利：对于Young区，gc线程多，那么处理快，会提高 Young 区处理时间，也就是降低 Young 区的 STW。
弊：需要考虑线程间通信、同步等各种由于多线程引入的问题。
```
* 调整二：增大堆整体内存，也就是 -Xmx
```plain
这个操作会等比例的增大 Young 区和 Old 区，造成的影响是 Young 区更大了，当然能容纳的对象就更多，那么就意味着 会减少 Young 的 GC 次数，同时也会增大 Young 区 GC 的 STW 时间，对于 Old 区的影响，这样做的话，每次 从 Young 区晋升到 Old 的存活对象自然就多了，那么对于 Old 的 GC，同样也会减少 FullGC 次数，但是 FullGC 的 STW 时间也会增加。
```
* 调整三：如何触发 FullGC

原理：

```plain
根据蓄水池思路，要想触发 FullGC，那么肯定就是得 Old 区做处理，尽量让 Old 区到达那个阈值，目标明确后，开始调整配置。
堆大小不变，设置 Young 区大，Old 区小 ，这样 Young 区晋升上来，直接就能把 Old 吃完内存，那么导致了 FullGC，也可能直接 OOM 了。
```
调整一：堆大小不变，设置 Young 区大，Old 小，这样 Young 区一晋升，Old 区内存就吃满了，触发 FullGC，还可能导致 OOM。
-Xmx1g -Xms1g -Xmn800M -XX:+PrintGCDetails -XX:+PrintGCDateStamps

```plain
正在执行...
2021-03-28T17:26:07.694+0800: [GC (Allocation Failure) [PSYoungGen: 614400K->102389K(716800K)] 614400K->160870K(946176K), 0.0465096 secs] [Times: user=0.03 sys=0.13, real=0.05 secs] 
2021-03-28T17:26:07.939+0800: [GC (Allocation Failure) [PSYoungGen: 716789K->102392K(716800K)] 775270K->285206K(946176K), 0.0734635 secs] [Times: user=0.08 sys=0.11, real=0.07 secs] 
2021-03-28T17:26:08.012+0800: [Full GC (Ergonomics) [PSYoungGen: 102392K->33228K(716800K)] [ParOldGen: 182814K->229367K(229376K)] 285206K->262596K(946176K), [Metaspace: 3410K->3410K(1056768K)], 0.0625225 secs] [Times: user=0.13 sys=0.08, real=0.06 secs] 
执行结束，共生成对象次数：6017
Heap
 PSYoungGen      total 716800K, used 473212K [0x00000000ce000000, 0x0000000100000000, 0x0000000100000000)
  eden space 614400K, 71% used [0x00000000ce000000,0x00000000e8dabf10,0x00000000f3800000)
  from space 102400K, 32% used [0x00000000f9c00000,0x00000000fbc731a8,0x0000000100000000)
  to   space 102400K, 0% used [0x00000000f3800000,0x00000000f3800000,0x00000000f9c00000)
 ParOldGen       total 229376K, used 229367K [0x00000000c0000000, 0x00000000ce000000, 0x00000000ce000000)
  object space 229376K, 99% used [0x00000000c0000000,0x00000000cdffde58,0x00000000ce000000)
 Metaspace       used 3759K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 416K, capacity 428K, committed 512K, reserved 1048576K
显然：2次 Young GC，Old 区就发生了 FullGC，看数据 Old 区FullGC 之后，内存还大了，第二次 Young 区GC的时候，这块要研究一下晋升机制，内存增大可能是 YoungGC 晋升了上来导致的，此时 YoungGC 和 FullGC 可能同时发生。
```
### 修改 GC 策略为 串行 GC（SerialGC）

#### 参数：设定初始堆和堆最大内存为1g

-XX:+UseSerialGC -Xmx1g -Xms1g -XX:+PrintGCDetails -XX:+PrintGCDateStamps

>串行 GC：两个代的处理都是单线程，年轻代使用的是 Copy 标记复制算法，老年代采用的是 MarkSweepCompact 标记清除整理算法，都会触发 STW。
>适合：单核服务器，几百m大小的堆
#### 日志信息

```plain
正在执行...
2021-03-28T21:16:21.934+0800: [GC (Allocation Failure) [DefNew: 279616K->34944K(314560K), 0.0482116 secs] 279616K->83226K(1013632K), 0.0482552 secs] [Times: user=0.03 sys=0.01, real=0.05 secs] 
2021-03-28T21:16:22.073+0800: [GC (Allocation Failure) [DefNew: 314560K->34943K(314560K), 0.0640642 secs] 362842K->159754K(1013632K), 0.0640924 secs] [Times: user=0.03 sys=0.03, real=0.06 secs] 
2021-03-28T21:16:22.250+0800: [GC (Allocation Failure) [DefNew: 314559K->34943K(314560K), 0.0618626 secs] 439370K->242643K(1013632K), 0.0618951 secs] [Times: user=0.03 sys=0.03, real=0.06 secs] 
2021-03-28T21:16:22.396+0800: [GC (Allocation Failure) [DefNew: 314559K->34943K(314560K), 0.0498991 secs] 522259K->313638K(1013632K), 0.0499308 secs] [Times: user=0.03 sys=0.00, real=0.05 secs] 
2021-03-28T21:16:22.576+0800: [GC (Allocation Failure) [DefNew: 314554K->34944K(314560K), 0.0488659 secs] 593249K->381331K(1013632K), 0.0488920 secs] [Times: user=0.03 sys=0.02, real=0.05 secs] 
执行结束，共生成对象次数：6226
Heap
 def new generation   total 314560K, used 314079K [0x00000000c0000000, 0x00000000d5550000, 0x00000000d5550000)
  eden space 279616K,  99% used [0x00000000c0000000, 0x00000000d1097e48, 0x00000000d1110000)
  from space 34944K, 100% used [0x00000000d3330000, 0x00000000d5550000, 0x00000000d5550000)
  to   space 34944K,   0% used [0x00000000d1110000, 0x00000000d1110000, 0x00000000d3330000)
 tenured generation   total 699072K, used 346387K [0x00000000d5550000, 0x0000000100000000, 0x0000000100000000)
   the space 699072K,  49% used [0x00000000d5550000, 0x00000000ea794f60, 0x00000000ea795000, 0x0000000100000000)
 Metaspace       used 3758K, capacity 4540K, committed 4864K, reserved 1056768K
  class space    used 416K, capacity 428K, committed 512K, reserved 1048576K
```
#### 分析

```plain
从 GC 的处理时间来看，单线程的 GC 策略明显比 多线程的处理要慢，=
```
### 修改 GC 策略为 CMS GC

#### 参数：设置初始堆和堆最大内存为1g

-XX:+UseConcMarkSweepGC -Xmx512M -Xms512M -XX:+PrintGCDetails -XX:+PrintGCDateStamps

>CMS GC，主要是考虑 STW 时业务线程暂停的情况，ParallelGC只是多加了 gc 线程，但是业务线程也会停，CMS 分为 6个阶段，第一阶段和第四阶段会存在 STW，因为要精确统计，其他四个阶段可以让 业务线程和gc线程 并发执行，减少了对业务的影响，提高吞吐量。
>-XX:+UseConcMarkSweepGC，也存在分代思想，年轻代用得 gc 方式是 ParNew，这是一种对 SerialGC 改进的 GC 算法，其实就是并行，多加线程，Old 区用得 GC 算法是 ConcurrentMarkSweep，并发标记清除算法
#### 日志信息

```plain
2021-03-28T21:28:42.475+0800: [GC (CMS Initial Mark) [1 CMS-initial-mark: 205195K(349568K)] 223355K(506816K), 0.0001873 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-03-28T21:28:42.476+0800: [CMS-concurrent-mark-start]
2021-03-28T21:28:42.481+0800: [CMS-concurrent-mark: 0.005/0.005 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-03-28T21:28:42.481+0800: [CMS-concurrent-preclean-start]
2021-03-28T21:28:42.482+0800: [CMS-concurrent-preclean: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-03-28T21:28:42.482+0800: [CMS-concurrent-abortable-preclean-start]
2021-03-28T21:28:42.553+0800: [GC (Allocation Failure) [ParNew2021-03-28T21:28:42.584+0800: [CMS-concurrent-abortable-preclean: 0.001/0.101 secs] [Times: user=0.03 sys=0.06, real=0.10 secs] 
: 157248K->17472K(157248K), 0.0340495 secs] 362443K->264612K(506816K), 0.0341009 secs] [Times: user=0.00 sys=0.06, real=0.04 secs] 
2021-03-28T21:28:42.588+0800: [GC (CMS Final Remark) [YG occupancy: 20446 K (157248 K)][Rescan (parallel) , 0.0007240 secs][weak refs processing, 0.0000385 secs][class unloading, 0.0014656 secs][scrub symbol table, 0.0019578 secs][scrub string table, 0.0002395 secs][1 CMS-remark: 247140K(349568K)] 267587K(506816K), 0.0047264 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-03-28T21:28:42.593+0800: [CMS-concurrent-sweep-start]
2021-03-28T21:28:42.594+0800: [CMS-concurrent-sweep: 0.001/0.001 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
2021-03-28T21:28:42.594+0800: [CMS-concurrent-reset-start]
2021-03-28T21:28:42.595+0800: [CMS-concurrent-reset: 0.001/0.001 secs] [Times: user=0.02 sys=0.00, real=0.00 secs] 
```
#### 分析

```plain
Young GC：采用了并行 GC 算法，处理的时间明显比单线程要快。
Old区：GC的时候，也会触发 Young 区GC，为了能准确统计，出现了四步去统计，
第一步对应：CMS Initial Mark，初始标记，看时间仅花了1毫秒，这个阶段会触发 STW，统计数据准确，
然后进入第二阶段，CMS-concurrent-mark，并发标记，这个阶段统计数据不准确，并发下可能因为YoungGC导致对象间引用关系复杂，
然后进入CMS-concurrent-preclean，这个阶段统计数据也不准确，基于上一步统计的结果再去遍历找对象间引用关系，不准确原因也是因为并发。
进入CMS Final Remark，最终标记，这块会触发 stw，因为要精确统计数据，这块 stw 时间比 第一阶段短。
然后进入清理阶段，CMS-concurrent-sweep，并发清理，开始处理垃圾
最后进入并发重置阶段，CMS-concurrent-reset，主要是为了下一次 CMS GC做准备。
```
### 修改 GC 策略为 G1

#### 参数和上述保持一致

>G1GC：没有分代这种思想，从更小粒度，将整个堆打成小块（region）来看，Young区采用了 G1 Young Generation 算法，Old 区采用了 G1 Old Generation 算法。
#### 日志信息

```plain
正在执行...
2021-03-28T21:37:43.721+0800: [GC pause (G1 Evacuation Pause) (young) 34M->10M(512M), 0.0026446 secs]
2021-03-28T21:37:43.734+0800: [GC pause (G1 Evacuation Pause) (young) 36M->20M(512M), 0.0038501 secs]
2021-03-28T21:37:43.750+0800: [GC pause (G1 Evacuation Pause) (young) 50M->31M(512M), 0.0049043 secs]
2021-03-28T21:37:43.786+0800: [GC pause (G1 Evacuation Pause) (young) 77M->46M(512M), 0.0042611 secs]
2021-03-28T21:37:43.826+0800: [GC pause (G1 Evacuation Pause) (young) 131M->77M(512M), 0.0067539 secs]
2021-03-28T21:37:43.864+0800: [GC pause (G1 Evacuation Pause) (young) 155M->100M(512M), 0.0057061 secs]
2021-03-28T21:37:44.130+0800: [GC pause (G1 Evacuation Pause) (young)-- 454M->375M(512M), 0.0035918 secs]
2021-03-28T21:37:44.135+0800: [GC pause (G1 Humongous Allocation) (young) (initial-mark) 378M->376M(512M), 0.0007386 secs]
2021-03-28T21:37:44.136+0800: [GC concurrent-root-region-scan-start]
2021-03-28T21:37:44.136+0800: [GC concurrent-root-region-scan-end, 0.0001997 secs]
2021-03-28T21:37:44.136+0800: [GC concurrent-mark-start]
2021-03-28T21:37:44.143+0800: [GC concurrent-mark-end, 0.0066615 secs]
2021-03-28T21:37:44.143+0800: [GC remark, 0.0015874 secs]
2021-03-28T21:37:44.145+0800: [GC cleanup 398M->382M(512M), 0.0003785 secs]
2021-03-28T21:37:44.145+0800: [GC concurrent-cleanup-start]
2021-03-28T21:37:44.145+0800: [GC concurrent-cleanup-end, 0.0000398 secs]
```
#### 分析

```plain
通过日志看，Young区是模式转移暂停，G1 GC会根据当前 JVM 运行 情况不断调整回收策略和行为，这个转移指的是年轻代满了，拷贝（转移）到存活区，这时候应用线程会暂停。
后面是并发标记阶段，和CMSGC比较类似。
```

---
## 

## 题目二：使用压测工具（wrk 或 sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 示例

### 采用 Serial GC 启动：-XX:+UseSerialGC

![图片](https://uploader.shimo.im/f/Kl7eLMzyh7rvgDG7.png!thumbnail?fileGuid=TWGW3KRw6RTd8DcY)

* 分析
```plain
每秒请求数是4127个左右，单请求延迟平均时间是13.96ms，最高达到了360ms，高于200ms，性能不是很好
```
### 采用 ParallelGC 启动：-XX:+UseParallelGC

![图片](https://uploader.shimo.im/f/rtsja8S4dpvPJ70K.png!thumbnail?fileGuid=TWGW3KRw6RTd8DcY)

* 分析
```plain
同样的环境，采用并行GC，发现并没有什么显著的性能提升
```
### 采用 CMS GC启动：-XX:+UseConcMarkSweepGC

![图片](https://uploader.shimo.im/f/nckopfKCLdFWtzCf.png!thumbnail?fileGuid=TWGW3KRw6RTd8DcY)

* 分析
```plain
同样环境，感觉除了最高延迟低了一点，其他地方也没怎么优化
```
### 采用 G1GC 启动：-XX:+UseG1GC

![图片](https://uploader.shimo.im/f/zSsj8urZ4jtYoG5l.png!thumbnail?fileGuid=TWGW3KRw6RTd8DcY)

* 分析
```plain
同等环境，感觉也没什么优化
```
### 扩展：修改JVM 堆配置，再测试


---


## 题目三：不同 GC 策略和堆内存的总结

### 前提

```plain
堆内存属于共享资源，稀缺，GC模块管理的也主要是堆区，堆区存放的主要是对象，对象又分为对象头和对象体，内存都可算。
依据蓄水池的思路，调整堆整体大小，或者是调整堆内部的内存分配，都可能导致GC执行的结果有出入。
```
### 串行 GC

```plain
单线程，对 堆区的 年轻代和老年代都是单线程的gc线程去处理，性能上较慢，如果堆区过大，那么会导致 gc 处理时长长，stw久，对业务影响也较大，所以说如果系统是比较追求响应时间和吞吐量的，不建议用 串行gc，串行gc适合单核Cpu，堆小的情况。
```
### 并行 GC

```plain
基于 串行 单线程这个导致性能的问题点，并行 GC 对不同代有不同策略，年轻代，选用了SerialGC的 改进版Parallel Scavenge，单线程为多线程，主要是年轻代做了并行处理，多gc线程，处理更快，提高系统的吞吐量，Old区还是单线程处理，这块也是基于弱代假设的，不停的监控和分析对象的生命周期，再基于得到这些指标之后去做优化。
两个代的gc 都会导致 stw。
```
### CMS GC

```plain
最大可能的并行的标记清除 GC，也是基于 并行GC不好的点做了改进，因为并行GC两个代都触发了STW，这就意味着当前系统仅有 gc 线程在使用CPU资源，业务线程等待中，CMS GC基于这个去分代，年轻代使用了并行GC去处理，old区选用了CMS，CMS6个阶段，1/4阶段是标记，23阶段是业务线程和gc线程并行处理，此时数据统计不准确，14阶段触发的stw目标是为了精确统计当前对象情况，5阶段是并发清理，不停摆业务线程，6阶段是为了下一次CMSGC做预热，整个堆其实还是被分代了，但是整体来看堆的利用率提高了，也就增大了系统的吞吐量。
```
### G1GC

```plain
引入了CMSGC，能在STW间做一个统一，此时JVM开发者的关注点，在内存大小上，认为分代处理的堆区过大，并发情况下对资源的锁定粒度问题，因此将堆内存分块，每一块大小统一，不分代，它引入了动态调整，每一块都处于动态状态，从eden状态，from状态，to状态，old状态切换，性能上进一步得到提高，更好的利用内存资源。
```
### ZGC：后续补充

### ShendoahGC：后续补充


---


## 题目四：通过 HttpClient 或 OkHttp 访问[Http://localhost:8081](Http://localhost:8081?fileGuid=TWGW3KRw6RTd8DcY)

```plain
代码见 code 文件夹
```
