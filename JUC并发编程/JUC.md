# JUC

## 进程与线程

### 概述

**进程**：程序是静止的，进程实体的运行过程就是进程，是系统进行**资源分配的基本单位**

进程的特征：并发性、异步性、动态性、独立性、结构性

**线程**：线程是属于进程的，是一个基本的 CPU 执行单元，是程序执行流的最小单元。线程是进程中的一个实体，是系统**独立调度的基本单位**，线程本身不拥有系统资源，只拥有一点在运行中必不可少的资源，与同属一个进程的其他线程共享进程所拥有的全部资源

关系：一个进程可以包含多个线程，这就是多线程，比如看视频是进程，图画、声音、广告等就是多个线程

线程的作用：使多道程序更好的并发执行，提高资源利用率和系统吞吐量，增强操作系统的并发性能

**并发并行：**

- 并行（parallel）：是同一时间动手做（doing）多件事情的能力
- 并发（concurrent）：是同一时间应对（dealing with）多件事情的能力

**同步异步：**

- 需要等待结果返回，才能继续运行就是同步
- 不需要等待结果返回，就能继续运行就是异步

### 对比

线程进程对比：

- 进程基本上相互独立的，而线程存在于进程内，是进程的一个子集

- 进程拥有共享的资源，如内存空间等，供其**内部的线程共享**

- 进程间通信较为复杂

  同一台计算机的进程通信称为 IPC（Inter-process communication）

  - 信号量：信号量是一个计数器，用于多进程对共享数据的访问，解决同步相关的问题并避免竞争条件

  - 共享存储：多个进程可以访问同一块内存空间，需要使用信号量用来同步对共享存储的访问

  - 管道通信：管道是用于连接一个读进程和一个写进程以实现它们之间通信的一个共享文件 pipe 文件，该文件同一时间只允许一个进程访问，所以只支持

    半双工通信

    - 匿名管道（Pipes）：用于具有亲缘关系的父子进程间或者兄弟进程之间的通信
    - 命名管道（Names Pipes）：以磁盘文件的方式存在，可以实现本机任意两个进程通信，遵循 FIFO

  - 消息队列：内核中存储消息的链表，由消息队列标识符标识，能在不同进程之间提供

    全双工通信

    对比管道：

    - 匿名管道存在于内存中的文件；命名管道存在于实际的磁盘介质或者文件系统；消息队列存放在内核中，只有在内核重启（操作系统重启）或者显示地删除一个消息队列时，该消息队列才被真正删除
    - 读进程可以根据消息类型有选择地接收消息，而不像 FIFO 那样只能默认地接收

  不同计算机之间的**进程通信**，需要通过网络，并遵守共同的协议，例如 HTTP

  - 套接字：与其它通信机制不同的是，可用于不同机器间的互相通信

- 线程通信相对简单，因为线程之间共享进程内的内存，一个例子是多个线程可以访问同一个共享变量

  **Java 中的通信机制**：volatile、等待/通知机制、join 方式、InheritableThreadLocal、MappedByteBuffer

- 线程更轻量，线程上下文切换成本一般上要比进程上下文切换低

## Java 线程

### 创建线程

#### Thread

Thread 创建线程方式：创建线程类，匿名内部类方式

- **start() 方法底层其实是给 CPU 注册当前线程，并且触发 run() 方法执行**
- 线程的启动必须调用 start() 方法，如果线程直接调用 run() 方法，相当于变成了普通类的执行，此时主线程将只有执行该线程
- 建议线程先创建子线程，主线程的任务放在之后，否则主线程（main）永远是先执行完

Thread 构造器：

- `public Thread()`
- `public Thread(String name)`

1. 匿名内部类方式

   ```java
   public class ThreadDemo{
       public static void main(String[] args) {
           Thread t = new Thread(){
               @Override
               public void run(){
                   System.out.println("running");
               }
           };
           t.setName("t1");
           t.start();
       }
   }
   ```

2. 创建线程类继承 Thread

   ```java
   public class ThreadDemo {
       public static void main(String[] args) {
           Thread t = new MyThread();
           t.start();
          	for(int i = 0 ; i < 100 ; i++ ){
               System.out.println("main线程" + i)
           }
           // main线程输出放在上面 就变成有先后顺序了，因为是 main 线程驱动的子线程运行
       }
   }
   class MyThread extends Thread {
       @Override
       public void run() {
           for(int i = 0 ; i < 100 ; i++ ) {
               System.out.println("子线程输出："+i)
           }
       }
   }
   ```

继承 Thread 类的优缺点：

- 优点：编码简单
- 缺点：线程类已经继承了 Thread 类无法继承其他类了，功能不能通过继承拓展（单继承的局限性）

#### Runnable

把【线程】和【任务】（要执行的代码）分开

Runnable 创建线程方式：创建线程类，匿名内部类方式

Thread 的构造器：

- `public Thread(Runnable target)`
- `public Thread(Runnable target, String name)`

```java
public class ThreadDemo{
    public static void main(String[] args) {
        // 创建线程对象
        Thread t = new Thread(new Runnable(){
            @Override
            public void run(){
                // 要执行的任务
            }
        };);
        // 启动线程
        t.start();
    }
}
```

```java
public class ThreadDemo {
    public static void main(String[] args) {
        Runnable target = new MyRunnable();
        Thread t1 = new Thread(target,"1号线程");
		t1.start();
        Thread t2 = new Thread(target);//Thread-0
    }
}

public class MyRunnable implements Runnable{
    @Override
    public void run() {
        for(int i = 0 ; i < 10 ; i++ ){
            System.out.println(Thread.currentThread().getName() + "->" + i);
        }
    }
}
```

**Thread 类本身也是实现了 Runnable 接口**，Thread 类中持有 Runnable 的属性，执行线程 run 方法底层是调用 Runnable#run：

```java
public class Thread implements Runnable {
    private Runnable target;
    
    public void run() {
        if (target != null) {
          	// 底层调用的是 Runnable 的 run 方法
            target.run();
        }
    }
}
```

Runnable 方式的优缺点：

- 缺点：代码复杂一点。
- 优点：
  1. 线程任务类只是实现了 Runnable 接口，可以继续继承其他类，避免了单继承的局限性
  2. 同一个线程任务对象可以被包装成多个线程对象
  3. 适合多个多个线程去共享同一个资源
  4. 实现解耦操作，线程任务代码可以被多个线程共享，线程任务代码和线程独立
  5. 线程池可以放入实现 Runnable 或 Callable 线程任务对象

#### Callable

实现 Callable 接口：

1. 定义一个线程任务类实现 Callable 接口，申明线程执行的结果类型
2. 重写线程任务类的 call 方法，这个方法可以直接返回执行的结果
3. 创建一个 Callable 的线程任务对象
4. 把 Callable 的线程任务对象**包装成一个未来任务对象**
5. 把未来任务对象包装成线程对象
6. 调用线程的 start() 方法启动线程

`public FutureTask(Callable<V> callable)`：未来任务对象，在线程执行完后得到线程的执行结果

- FutureTask 就是 Runnable 对象，因为 **Thread 类只能执行 Runnable 实例的任务对象**，所以把 Callable 包装成未来任务对象
- 线程池部分详解了 FutureTask 的源码

`public V get()`：同步等待 task 执行完毕的结果，如果在线程中获取另一个线程执行结果，会阻塞等待，用于线程同步

- get() 线程会阻塞等待任务执行完成
- run() 执行完后会把结果设置到 FutureTask 的一个成员变量，get() 线程可以获取到该变量的值

优缺点：

- 优点：同 Runnable，并且能得到线程执行的结果
- 缺点：编码复杂

```java
public class ThreadDemo {
    public static void main(String[] args) {
        Callable call = new MyCallable();
        FutureTask<String> task = new FutureTask<>(call);
        Thread t = new Thread(task);
        t.start();
        try {
            String s = task.get(); // 获取call方法返回的结果（正常/异常结果）
            System.out.println(s);
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }

public class MyCallable implements Callable<String> {
    @Override//重写线程任务类方法
    public String call() throws Exception {
        return Thread.currentThread().getName() + "->" + "Hello World";
    }
}
```

### 线程原理

#### 运行机制

**Java Virtual Machine Stacks（Java 虚拟机栈）**：每个线程启动后，虚拟机就会为其分配一块栈内存

- 每个栈由多个栈帧（Frame）组成，对应着每次方法调用时所占用的内存
- 每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法

**线程上下文切换（Thread Context Switch）**：一些原因导致 CPU 不再执行当前线程，转而执行另一个线程

- 线程的 CPU 时间片用完
- 垃圾回收
- 有更高优先级的线程需要运行
- 线程自己调用了 sleep、yield、wait、join、park 等方法

**程序计数器（Program Counter Register）**：记住下一条 JVM 指令的执行地址，是线程私有的

当 Context Switch 发生时，需要由操作系统保存当前线程的状态（PCB 中），并恢复另一个线程的状态，包括程序计数器、虚拟机栈中每个栈帧的信息，如局部变量、操作数栈、返回地址等

JVM 规范并没有限定线程模型，以 HotSopot 为例：

- Java 的线程是内核级线程（1:1 线程模型），每个 Java 线程都映射到一个操作系统原生线程，需要消耗一定的内核资源（堆栈）
- **线程的调度是在内核态运行的，而线程中的代码是在用户态运行**，所以线程切换（状态改变）会导致用户与内核态转换进行系统调用，这是非常消耗性能

Java 中 main 方法启动的是一个进程也是一个主线程，main 方法里面的其他线程均为子线程，main 线程是这些线程的父线程

------

### 查看线程

Windows：

- 任务管理器可以查看进程和线程数，也可以用来杀死进程
- tasklist 查看进程
- taskkill 杀死进程

Linux：

- ps -ef 查看所有进程
- ps -fT -p 查看某个进程（PID）的所有线程
- kill 杀死进程
- top 按大写 H 切换是否显示线程
- top -H -p 查看某个进程（PID）的所有线程

Java：

- jps 命令查看所有 Java 进程
- jstack 查看某个 Java 进程（PID）的所有线程状态
- jconsole 来查看某个 Java 进程中线程的运行情况（图形界面）

------

### 线程状态

进程的状态参考操作系统：创建态、就绪态、运行态、阻塞态、终止态

线程由生到死的完整过程（生命周期）：当线程被创建并启动以后，既不是一启动就进入了执行状态，也不是一直处于执行状态，在 API 中 `java.lang.Thread.State` 这个枚举中给出了六种线程状态：

| 线程状态                   | 导致状态发生条件                                             |
| -------------------------- | ------------------------------------------------------------ |
| NEW（新建）                | 线程刚被创建，但是并未启动，还没调用 start 方法，只有线程对象，没有线程特征 |
| Runnable（可运行）         | 线程可以在 Java 虚拟机中运行的状态，可能正在运行自己代码，也可能没有，这取决于操作系统处理器，调用了 t.start() 方法：就绪（经典叫法） |
| Blocked（阻塞）            | 当一个线程试图获取一个对象锁，而该对象锁被其他的线程持有，则该线程进入 Blocked 状态；当该线程持有锁时，该线程将变成 Runnable 状态 |
| Waiting（无限等待）        | 一个线程在等待另一个线程执行一个（唤醒）动作时，该线程进入 Waiting 状态，进入这个状态后不能自动唤醒，必须等待另一个线程调用 notify 或者 notifyAll 方法才能唤醒 |
| Timed Waiting （限期等待） | 有几个方法有超时参数，调用将进入 Timed Waiting 状态，这一状态将一直保持到超时期满或者接收到唤醒通知。带有超时参数的常用方法有 Thread.sleep 、Object.wait |
| Teminated（结束）          | run 方法正常退出而死亡，或者因为没有捕获的异常终止了 run 方法而死亡 |

![image-20221214153932466](JUC.assets/image-20221214153932466.png)

- NEW → RUNNABLE：当调用 t.start() 方法时，由 NEW → RUNNABLE

- RUNNABLE <--> WAITING：

  - 调用 obj.wait() 方法时

    调用 obj.notify()、obj.notifyAll()、t.interrupt()：

    - 竞争锁成功，t 线程从 WAITING → RUNNABLE
    - 竞争锁失败，t 线程从 WAITING → BLOCKED

  - 当前线程调用 t.join() 方法，注意是当前线程在 t 线程对象的监视器上等待

    * t 线程结束，或调用了当前线程的 interrupt() 时，当前线程从 WAITING --> RUNNABLE

  - 当前线程调用 LockSupport.park() 方法

    * 调用LockSupport.unpark(目标线程) 或调用了线程的 interrupt()，会让目标线程从 WAITING --> RUNNABLE

- RUNNABLE <--> TIMED_WAITING：调用 obj.wait(long n) 方法、当前线程调用 t.join(long n) 方法、当前线程调用 Thread.sleep(long n)、当前线程调用 LockSupport.parkNanos(long nanos) 或 LockSupport.parkUntil(long millis)

- RUNNABLE <--> BLOCKED：t 线程用 synchronized(obj) 获取了对象锁时竞争失败

- RUNNABLE-->TERMINATED: 当前线程所有代码运行完毕，进入TERMINATED

------

#### 线程调度

线程调度指系统为线程分配处理器使用权的过程，方式有两种：协同式线程调度、抢占式线程调度（Java 选择）

协同式线程调度：线程的执行时间由线程本身控制

- 优点：线程做完任务才通知系统切换到其他线程，相当于所有线程串行执行，不会出现线程同步问题
- 缺点：线程执行时间不可控，如果代码编写出现问题，可能导致程序一直阻塞，引起系统的奔溃

抢占式线程调度：线程的执行时间由系统分配

- 优点：线程执行时间可控，不会因为一个线程的问题而导致整体系统不可用
- 缺点：无法主动为某个线程多分配时间

Java 提供了线程优先级的机制，优先级会提示（hint）调度器优先调度该线程，但这仅仅是一个提示，调度器可以忽略它。在线程的就绪状态时，如果 CPU 比较忙，那么优先级高的线程会获得更多的时间片，但 CPU 闲时，优先级几乎没作用

说明：并不能通过优先级来判断线程执行的先后顺序

------

#### 未来优化

内核级线程调度的成本较大，所以引入了更轻量级的协程。用户线程的调度由用户自己实现（多对一的线程模型，多**个用户线程映射到一个内核级线程**），被设计为协同式调度，所以叫协程

- 有栈协程：协程会完整的做调用栈的保护、恢复工作，所以叫有栈协程
- 无栈协程：本质上是一种有限状态机，状态保存在闭包里，比有栈协程更轻量，但是功能有限

有栈协程中有一种特例叫纤程，在新并发模型中，一段纤程的代码被分为两部分，执行过程和调度器：

- 执行过程：用于维护执行现场，保护、恢复上下文状态
- 调度器：负责编排所有要执行的代码顺序

------

### 线程方法

#### API

Thread 类 API：

| 方法                                        | 说明                                                         |
| ------------------------------------------- | ------------------------------------------------------------ |
| public void start()                         | 启动一个新线程，Java虚拟机调用此线程的 run 方法              |
| public void run()                           | 线程启动后调用该方法                                         |
| public void setName(String name)            | 给当前线程取名字                                             |
| public void getName()                       | 获取当前线程的名字 线程存在默认名称：子线程是 Thread-索引，主线程是 main |
| public static Thread currentThread()        | 获取当前线程对象，代码在哪个线程中执行                       |
| public static void sleep(long time)         | 让当前线程休眠多少毫秒再继续执行 **Thread.sleep(0)** : 让操作系统立刻重新进行一次 CPU 竞争 |
| public static native void yield()           | 提示线程调度器让出当前线程对 CPU 的使用                      |
| public final int getPriority()              | 返回此线程的优先级                                           |
| public final void setPriority(int priority) | 更改此线程的优先级，常用 1 5 10                              |
| public void interrupt()                     | 中断这个线程，异常处理机制                                   |
| public static boolean interrupted()         | 判断当前线程是否被打断，清除打断标记                         |
| public boolean isInterrupted()              | 判断当前线程是否被打断，不清除打断标记                       |
| public final void join()                    | 等待这个线程结束                                             |
| public final void join(long millis)         | 等待这个线程死亡 millis 毫秒，0 意味着永远等待               |
| public final native boolean isAlive()       | 线程是否存活（还没有运行完毕）                               |
| public final void setDaemon(boolean on)     | 将此线程标记为守护线程或用户线程                             |

------

#### run start

run：称为线程体，包含了要执行的这个线程的内容，方法运行结束，此线程随即终止。直接调用 run 是在主线程中执行了 run，没有启动新的线程，需要顺序执行

start：使用 start 是启动新的线程，此线程处于就绪（可运行）状态，通过新的线程间接执行 run 中的代码

说明：**线程控制资源类**

run() 方法中的异常不能抛出，只能 try/catch

- 因为父类中没有抛出任何异常，子类不能比父类抛出更多的异常
- **异常不能跨线程传播回 main() 中**，因此必须在本地进行处理

------

#### sleep yield

sleep：

- 调用 sleep 会让当前线程从 `Running` 进入 `Timed Waiting` 状态（阻塞）
- sleep() 方法的过程中，**线程不会释放对象锁**
- 其它线程可以使用 interrupt 方法打断正在睡眠的线程，这时 sleep 方法会抛出 InterruptedException
- 睡眠结束后的线程未必会立刻得到执行，需要抢占 CPU
- 建议用 TimeUnit 的 sleep 代替 Thread 的 sleep 来获得更好的可读性

yield：

- 调用 yield 会让提示线程调度器让出当前线程对 CPU 的使用
- 具体的实现依赖于操作系统的任务调度器
- **会放弃 CPU 资源，锁资源不会释放**

------

#### join

public final void join()：等待这个线程结束

原理：调用者轮询检查线程 alive 状态，t1.join() 等价于：

```java
public final synchronized void join(long millis) throws InterruptedException {
    // 调用者线程进入 thread 的 waitSet 等待, 直到当前线程运行结束
    while (isAlive()) {
        wait(0);
    }
}
```

- join 方法是被 synchronized 修饰的，本质上是一个对象锁，其内部的 wait 方法调用也是释放锁的，但是**释放的是当前的线程对象锁，而不是外面的锁**
- 当调用某个线程（t1）的 join 方法后，该线程（t1）抢占到 CPU 资源，就不再释放，直到线程执行完毕

线程同步：

- join 实现线程同步，因为会阻塞等待另一个线程的结束，才能继续向下运行
  - 需要外部共享变量，不符合面向对象封装的思想
  - 必须等待线程结束，不能配合线程池使用
- Future 实现（同步）：get() 方法阻塞等待执行结果
  - main 线程接收结果
  - get 方法是让调用线程同步等待

```java
public class Test {
    static int r = 0;
    public static void main(String[] args) throws InterruptedException {
        test1();
    }
    private static void test1() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            r = 10;
        });
        t1.start();
        t1.join();//不等待线程执行结束，输出的10
        System.out.println(r);
    }
}
```

------

#### interrupt

##### 打断线程

`public void interrupt()`：打断这个线程，异常处理机制

`public static boolean interrupted()`：判断当前线程是否被打断，打断返回 true，**清除打断标记**，连续调用两次一定返回 false

`public boolean isInterrupted()`：判断当前线程是否被打断，不清除打断标记

打断的线程会发生上下文切换，操作系统会保存线程信息，抢占到 CPU 后会从中断的地方接着运行（打断不是停止）

- sleep、wait、join 方法都会让线程进入阻塞状态，打断线程**会清空打断状态**（false）

  ```java
  public static void main(String[] args) throws InterruptedException {
      Thread t1 = new Thread(()->{
          try {
              Thread.sleep(1000);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }, "t1");
      t1.start();
      Thread.sleep(500);
      t1.interrupt();
      System.out.println(" 打断状态: {}" + t1.isInterrupted());// 打断状态: {}false
  }
  ```

- 打断正常运行的线程：不会清空打断状态（true）

  ```java
  public static void main(String[] args) throws Exception {
      Thread t2 = new Thread(()->{
          while(true) {
              Thread current = Thread.currentThread();
              boolean interrupted = current.isInterrupted();
              if(interrupted) {
                  System.out.println(" 打断状态: {}" + interrupted);//打断状态: {}true
                  break;
              }
          }
      }, "t2");
      t2.start();
      Thread.sleep(500);
      t2.interrupt();
  }
  ```

------

##### 打断 park

park 作用类似 sleep，打断 park 线程，不会清空打断状态（true）

```java
public static void main(String[] args) throws Exception {
    Thread t1 = new Thread(() -> {
        System.out.println("park...");
        LockSupport.park();
        System.out.println("unpark...");
        System.out.println("打断状态：" + Thread.currentThread().isInterrupted());//打断状态：true
    }, "t1");
    t1.start();
    Thread.sleep(2000);
    t1.interrupt();
}
```

如果打断标记已经是 true, 则 park 会失效

```java
LockSupport.park();
System.out.println("unpark...");
LockSupport.park();//失效，不会阻塞
System.out.println("unpark...");//和上一个unpark同时执行
```

可以修改获取打断状态方法，使用 `Thread.interrupted()`，清除打断标记

LockSupport 类在 同步 → park-un 详解

------

##### 终止模式

终止模式之两阶段终止模式：Two Phase Termination

目标：在一个线程 T1 中如何优雅终止线程 T2？优雅指的是给 T2 一个后置处理器

错误思想：

- 使用线程对象的 stop() 方法停止线程：stop 方法会真正杀死线程，如果这时线程锁住了共享资源，当它被杀死后就再也没有机会释放锁，其它线程将永远无法获取锁
- 使用 System.exit(int) 方法停止线程：目的仅是停止一个线程，但这种做法会让整个程序都停止

两阶段终止模式图示：

<img src="JUC.assets/image-20221214144229379.png" alt="image-20221214144229379" style="zoom:67%;" />

打断线程可能在任何时间，所以需要考虑在任何时刻被打断的处理方法：

```java
public class Test {
    public static void main(String[] args) throws InterruptedException {
        TwoPhaseTermination tpt = new TwoPhaseTermination();
        tpt.start();
        Thread.sleep(3500);
        tpt.stop();
    }
}
class TwoPhaseTermination {
    private Thread monitor;
    // 启动监控线程
    public void start() {
        monitor = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Thread thread = Thread.currentThread();
                    if (thread.isInterrupted()) {
                        System.out.println("后置处理");
                        break;
                    }
                    try {
                        Thread.sleep(1000);					// 睡眠
                        System.out.println("执行监控记录");	// 在此被打断不会异常
                    } catch (InterruptedException e) {		// 在睡眠期间被打断，进入异常处理的逻辑
                        e.printStackTrace();
                        // 重新设置打断标记，打断 sleep 会清除打断状态
                        thread.interrupt();
                    }
                }
            }
        });
        monitor.start();
    }
    // 停止监控线程
    public void stop() {
        monitor.interrupt();
    }
}
```

------

#### daemon

`public final void setDaemon(boolean on)`：如果是 true ，将此线程标记为守护线程

线程**启动前**调用此方法：

```java
Thread t = new Thread() {
    @Override
    public void run() {
        System.out.println("running");
    }
};
// 设置该线程为守护线程
t.setDaemon(true);
t.start();
```

用户线程：平常创建的普通线程

守护线程：服务于用户线程，只要其它非守护线程运行结束了，即使守护线程代码没有执行完，也会强制结束。守护进程是**脱离于终端并且在后台运行的进程**，脱离终端是为了避免在执行的过程中的信息在终端上显示

说明：当运行的线程都是守护线程，Java 虚拟机将退出，因为普通线程执行完后，JVM 是守护线程，不会继续运行下去

常见的守护线程：

- 垃圾回收器线程就是一种守护线程
- Tomcat 中的 Acceptor 和 Poller 线程都是守护线程，所以 Tomcat 接收到 shutdown 命令后，不会等待它们处理完当前请求

------

#### 不推荐

不推荐使用的方法，这些方法已过时，容易破坏同步代码块，造成线程死锁：

- `public final void stop()`：停止线程运行

  废弃原因：方法粗暴，除非可能执行 finally 代码块以及释放 synchronized 外，线程将直接被终止，如果线程持有 JUC 的互斥锁可能导致锁来不及释放，造成其他线程永远等待的局面

- `public final void suspend()`：**挂起（暂停）线程运行**

  废弃原因：如果目标线程在暂停时对系统资源持有锁，则在目标线程恢复之前没有线程可以访问该资源，如果**恢复目标线程的线程**在调用 resume 之前会尝试访问此共享资源，则会导致死锁

- `public final void resume()`：恢复线程运行

------

## 共享模型之管程

### 临界区 Critical Section

临界资源：一次仅允许一个进程使用的资源成为临界资源

临界区：访问临界资源的代码块

* 一个程序运行多个线程是没有问题
* 问题出在多个线程访问共享资源
  * 多个线程读共享资源也没有问题
  * 在多个线程对共享资源读写操作时发生指令交错，就会出现问题

### 竞态条件 Race Condition

多个线程在临界区内执行，由于代码的执行序列不同而导致结果无法预测，称之为发生了竞态条件

### 解决方案

为了避免临界区的竞态条件发生（解决线程安全问题）：

- 阻塞式的解决方案：synchronized，lock
- 非阻塞式的解决方案：原子变量

管程（monitor）：由局部于自己的若干公共变量和所有访问这些公共变量的过程所组成的软件模块，保证同一时刻只有一个进程在管程内活动，即管程内定义的操作在同一时刻只被一个进程调用（由编译器实现）

**synchronized：对象锁，保证了临界区内代码的原子性**，采用互斥的方式让同一时刻至多只有一个线程能持有对象锁，其它线程获取这个对象锁时会阻塞，保证拥有锁的线程可以安全的执行临界区内的代码，不用担心线程上下文切换

互斥和同步都可以采用 synchronized 关键字来完成，区别：

- 互斥是保证临界区的竞态条件发生，同一时刻只能有一个线程执行临界区代码
- 同步是由于线程执行的先后、顺序不同、需要一个线程等待其它线程运行到某个点

性能：

- 线程安全，性能差
- 线程不安全性能好，假如开发中不会存在多线程安全问题，建议使用线程不安全的设计类

------

### synchronized

#### 使用锁

##### 同步块

锁对象：理论上可以是**任意的唯一对象**

synchronized 是可重入、不公平的重量级锁

原则上：

- 锁对象建议使用共享资源
- 在实例方法中使用 this 作为锁对象，锁住的 this 正好是共享资源
- 在静态方法中使用类名 .class 字节码作为锁对象，因为静态成员属于类，被所有实例对象共享，所以需要锁住类

同步代码块格式：

```java
synchronized(锁对象){
	// 访问共享资源的核心代码
}
```

实例：

```java
public class demo {
    static int counter = 0;
    //static修饰，则元素是属于类本身的，不属于对象  ，与类一起加载一次，只有一个
    static final Object room = new Object();
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (room) {
                    counter++;
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (room) {
                    counter--;
                }
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(counter);
    }
}
```

------

##### 同步方法

把出现线程安全问题的核心方法锁起来，每次只能一个线程进入访问

synchronized 修饰的方法的不具备继承性，所以子类是线程不安全的，如果子类的方法也被 synchronized 修饰，两个锁对象其实是一把锁，而且是**子类对象作为锁**

用法：直接给方法加上一个修饰符 synchronized

```java
//同步方法
修饰符 synchronized 返回值类型 方法名(方法参数) { 
	方法体；
}
//同步静态方法
修饰符 static synchronized 返回值类型 方法名(方法参数) { 
	方法体；
}
```

同步方法底层也是有锁对象的：

- 如果方法是实例方法：同步方法默认用 this 作为的锁对象

  ```java
  public synchronized void test() {} 
  //等价于
  public void test() {
      synchronized(this) {}
  }
  ```

- 如果方法是静态方法：同步方法默认用类名 .class 作为的锁对象

  ```java
  class Test{
  	public synchronized static void test() {}
  }
  //等价于
  class Test{
      public void test() {
          synchronized(Test.class) {}
  	}
  }
  ```

------

##### 线程八锁

线程八锁就是考察 synchronized 锁住的是哪个对象，直接百度搜索相关的实例

说明：主要关注锁住的对象是不是同一个

- 锁住类对象，所有类的实例的方法都是安全的，类的所有实例都相当于同一把锁
- 锁住 this 对象，只有在当前实例对象的线程内是安全的，如果有多个实例就不安全



以下例子线程不安全：因为锁住的不是同一个对象，线程 1 调用 a 方法锁住的类对象，线程 2 调用 b 方法锁住的 n2 对象，不是同一个对象

```java
class Number{
    public static synchronized void a(){
		Thread.sleep(1000);
        System.out.println("1");
    }
    public synchronized void b() {
        System.out.println("2");
    }
}
public static void main(String[] args) {
    Number n1 = new Number();
    Number n2 = new Number();
    new Thread(()->{ n1.a(); }).start();
    new Thread(()->{ n2.b(); }).start();
}
```

以下例子线程安全：因为 n1 调用 a() 方法，锁住的是类对象，n2 调用 b() 方法，锁住的也是类对象，所以线程安全

```java
class Number{
    public static synchronized void a(){
		Thread.sleep(1000);
        System.out.println("1");
    }
    public static synchronized void b() {
        System.out.println("2");
    }
}
public static void main(String[] args) {
    Number n1 = new Number();
    Number n2 = new Number();
    new Thread(()->{ n1.a(); }).start();
    new Thread(()->{ n2.b(); }).start();
}
```

------

### 常见线程安全类

* String
* Integer
* StringBuffer
* Random
* Vector
* Hashtable
* java.util.concurrent 包下的类

这里说它们是线程安全的是指，多个线程协调调用它们同一实例的某个方法时，是线程安全的。也可以理解为：

* 它们的每个方法是原子的
* 但注意它们多个方法的组合不是原子的

**不可变类线程安全性**

String、Integer等都是不可变类，因为其内部的状态不可以改变，因此它们的方法都是线程安全的

------

#### 锁原理

##### Monitor

Monitor 被翻译为监视器或管程

每个 Java 对象都可以关联一个 Monitor 对象，Monitor 也是 class，其**实例存储在堆中**，如果使用 synchronized 给对象上锁（重量级）之后，该对象头的 Mark Word 中就被设置指向 Monitor 对象的指针，这就是重量级锁

**JAVA对象头**

以32位虚拟机为例

* 普通对象

  ![image-20221215152931044](JUC.assets/image-20221215152931044.png)

- 数组对象

  ![image-20221215153001246](JUC.assets/image-20221215153001246.png)

- Mark Word 结构：最后两位是**锁标志位**

  ![image-20221215153346176](JUC.assets/image-20221215153346176.png)

Monitor结构如下

![image-20221215153549073](JUC.assets/image-20221215153549073.png)

- 开始时 Monitor 中 Owner 为 null

- 当 Thread-2 执行 synchronized(obj) 就会将 Monitor 的所有者 Owner 置为 Thread-2，Monitor 中只能有一个 Owner，**obj 对象的 Mark Word 指向 Monitor**，把**对象原有的 MarkWord 存入线程栈中的锁记录**中（轻量级锁部分详解）

  ![image-20221215155940004](JUC.assets/image-20221215155940004.png)

- 在 Thread-2 上锁的过程，Thread-3、Thread-4、Thread-5 也执行 synchronized(obj)，就会进入 EntryList BLOCKED（双向链表）
- Thread-2 执行完同步代码块的内容，根据 obj 对象头中 Monitor 地址寻找，设置 Owner 为空，把线程栈的锁记录中的对象头的值设置回 MarkWord
- 唤醒 EntryList 中等待的线程来竞争锁，竞争是**非公平的**，如果这时有新的线程想要获取锁，可能直接就抢占到了，阻塞队列的线程就会继续阻塞
- WaitSet 中的 Thread-0，是以前获得过锁，但条件不满足进入 WAITING 状态的线程（wait-notify 机制）

注意：

- synchronized 必须是进入同一个对象的 Monitor 才有上述的效果
- 不加 synchronized 的对象不会关联监视器，不遵从以上规则

------

##### 字节码

代码：

```java
public static void main(String[] args) {
    Object lock = new Object();
    synchronized (lock) {
        System.out.println("ok");
    }
}
```

```
0: 	new				#2		// new Object
3: 	dup
4: 	invokespecial 	#1 		// invokespecial <init>:()V，非虚方法
7: 	astore_1 				// lock引用 -> lock
8: 	aload_1					// lock （synchronized开始）
9: 	dup						// 一份用来初始化，一份用来引用
10: astore_2 				// lock引用 -> slot 2
11: monitorenter 			// 【将 lock对象 MarkWord 置为 Monitor 指针】
12: getstatic 		#3		// System.out
15: ldc 			#4		// "ok"
17: invokevirtual 	#5 		// invokevirtual println:(Ljava/lang/String;)V
20: aload_2 				// slot 2(lock引用)
21: monitorexit 			// 【将 lock对象 MarkWord 重置, 唤醒 EntryList】
22: goto 30
25: astore_3 				// any -> slot 3
26: aload_2 				// slot 2(lock引用)
27: monitorexit 			// 【将 lock对象 MarkWord 重置, 唤醒 EntryList】
28: aload_3
29: athrow
30: return
Exception table:
    from to target type
      12 22 25 		any
      25 28 25 		any
LineNumberTable: ...
LocalVariableTable:
    Start Length Slot Name Signature
    	0 	31 		0 args [Ljava/lang/String;
    	8 	23 		1 lock Ljava/lang/Object;
```

说明：

- 通过异常 **try-catch 机制**，确保一定会被解锁
- 方法级别的 synchronized 不会在字节码指令中有所体现

------

#### 锁升级

##### 升级过程

**synchronized 是可重入、不公平的重量级锁**，所以可以对其进行优化

```
无锁 -> 偏向锁 -> 轻量级锁 -> 重量级锁	// 随着竞争的增加，只能锁升级，不能降级
```

![image-20221215165232482](JUC.assets/image-20221215165232482.png)

##### 轻量级锁

一个对象有多个线程要加锁，但加锁的时间是错开的（没有竞争），可以使用轻量级锁来优化，轻量级锁对使用者是透明的（不可见）

可重入锁：线程可以进入任何一个它已经拥有的锁所同步着的代码块，可重入锁最大的作用是**避免死锁**

轻量级锁在没有竞争时（锁重入时），每次重入仍然需要执行 CAS 操作，Java 6 才引入的偏向锁来优化

锁重入实例：

```java
static final Object obj = new Object();
public static void method1() {
    synchronized( obj ) {
        // 同步块 A
        method2();
    }
}
public static void method2() {
    synchronized( obj ) {
    	// 同步块 B
    }
}
```

创建锁记录（Lock Record）对象，每个线程的**栈帧**都会包含一个锁记录的结构，存储锁定对象的 Mark Word

![image-20221215183114971](JUC.assets/image-20221215183114971.png)

- 让锁记录中 Object reference 指向锁住的对象，并尝试用 CAS 替换 Object 的 Mark Word，将 Mark Word 的值存入锁记录
- 如果 CAS 替换成功，对象头中存储了锁记录地址和状态 00（轻量级锁） ，表示由该线程给对象加锁

![image-20221215183714102](JUC.assets/image-20221215183714102.png)

如果 CAS 失败，有两种情况：

- 如果是其它线程已经持有了该 Object 的轻量级锁，这时表明有竞争，进入锁膨胀过程
- 如果是线程自己执行了 synchronized 锁重入，就添加一条 Lock Record 作为重入的计数

![image-20221215185242362](JUC.assets/image-20221215185242362.png)

当退出 synchronized 代码块（解锁时）

- 如果有取值为 null 的锁记录，表示有重入，这时重置锁记录，表示重入计数减 1

![image-20221215185444027](JUC.assets/image-20221215185444027.png)

当退出 synchronized 代码块（解锁时）锁记录的值不为 null，这时使用 CAS 将 Mark Word 的值恢复给对象头

- 成功，则解锁成功
- 失败，说明轻量级锁进行了锁膨胀或已经升级为重量级锁，进入重量级锁解锁流程

##### 锁膨胀

在尝试加轻量级锁的过程中，CAS 操作无法成功，可能是其它线程为此对象加上了轻量级锁（有竞争），这时需要进行锁膨胀，将轻量级锁变为**重量级锁**

- 当 Thread-1 进行轻量级加锁时，Thread-0 已经对该对象加了轻量级锁

![image-20221215212631976](JUC.assets/image-20221215212631976.png)

* Thread-1 加轻量级锁失败，进入锁膨胀流程：为 Object 对象申请 Monitor 锁，**通过 Object 对象头获取到持锁线程**，将 Monitor 的 Owner 置为 Thread-0，将 Object 的对象头指向重量级锁地址，然后自己进入 Monitor 的 EntryList BLOCKED

![image-20221215213407273](JUC.assets/image-20221215213407273.png)

- 当 Thread-0 退出同步块解锁时，使用 CAS 将 Mark Word 的值恢复给对象头失败，这时进入重量级解锁流程，即按照 Monitor 地址找到 Monitor 对象，设置 Owner 为 null，唤醒 EntryList 中 BLOCKED 线程

-----

#### 锁优化

##### 自旋锁

重量级锁竞争时，尝试获取锁的线程不会立即阻塞，可以使用**自旋**（默认 10 次）来进行优化，采用循环的方式去尝试获取锁

注意：

- 自旋占用 CPU 时间，单核 CPU 自旋就是浪费时间，因为同一时刻只能运行一个线程，多核 CPU 自旋才能发挥优势
- 自旋失败的线程会进入阻塞状态

优点：不会进入阻塞状态，**减少线程上下文切换的消耗**

缺点：当自旋的线程越来越多时，会不断的消耗 CPU 资源

**自旋锁情况**

* 自旋成功的情况：

![image-20221215214035935](JUC.assets/image-20221215214035935.png)

* 自旋失败的情况：

![image-20221215215018207](JUC.assets/image-20221215215018207.png)

自旋锁说明：

- 在 Java 6 之后自旋锁是自适应的，比如对象刚刚的一次自旋操作成功过，那么认为这次自旋成功的可能性会高，就多自旋几次；反之，就少自旋甚至不自旋，比较智能
- Java 7 之后不能控制是否开启自旋功能，由 JVM 控制

```java
//手写自旋锁
public class SpinLock {
    // 泛型装的是Thread，原子引用线程
    AtomicReference<Thread> atomicReference = new AtomicReference<>();

    public void lock() {
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName() + " come in");

        //开始自旋，期望值为null，更新值是当前线程
        while (!atomicReference.compareAndSet(null, thread)) {
            Thread.sleep(1000);
            System.out.println(thread.getName() + " 正在自旋");
        }
        System.out.println(thread.getName() + " 自旋成功");
    }

    public void unlock() {
        Thread thread = Thread.currentThread();

        //线程使用完锁把引用变为null
		atomicReference.compareAndSet(thread, null);
        System.out.println(thread.getName() + " invoke unlock");
    }

    public static void main(String[] args) throws InterruptedException {
        SpinLock lock = new SpinLock();
        new Thread(() -> {
            //占有锁
            lock.lock();
            Thread.sleep(10000); 

            //释放锁
            lock.unlock();
        },"t1").start();

        // 让main线程暂停1秒，使得t1线程，先执行
        Thread.sleep(1000);

        new Thread(() -> {
            lock.lock();
            lock.unlock();
        },"t2").start();
    }
}
```

------

##### 偏向锁

<img src="JUC.assets/image-20221215215623412.png" alt="image-20221215215623412" style="zoom:60%;" />

<img src="JUC.assets/image-20221215215806415.png" alt="image-20221215215806415" style="zoom:60%;" />

偏向锁的思想是偏向于让第一个获取锁对象的线程，这个线程之后重新获取该锁不再需要同步操作：

- 当锁对象第一次被线程获得的时候进入偏向状态，标记为 101，同时**使用 CAS 操作将线程 ID 记录到 Mark Word**。如果 CAS 操作成功，这个线程以后进入这个锁相关的同步块，查看这个线程 ID 是自己的就表示没有竞争，就不需要再进行任何同步操作
- 当有另外一个线程去尝试获取这个锁对象时，偏向状态就宣告结束，此时撤销偏向（Revoke Bias）后恢复到未锁定或轻量级锁状态



回忆一下对象头格式：

![image-20221215215856558](JUC.assets/image-20221215215856558.png)

一个对象创建时：

- 如果开启了偏向锁（默认开启），那么对象创建后，MarkWord 值为 0x05 即最后 3 位为 101，thread、epoch、age 都为 0
- 偏向锁是默认是延迟的，不会在程序启动时立即生效，如果想避免延迟，可以加 VM 参数 `-XX:BiasedLockingStartupDelay=0` 来禁用延迟。JDK 8 延迟 4s 开启偏向锁原因：在刚开始执行代码时，会有好多线程来抢锁，如果开偏向锁效率反而降低
- 当一个对象已经计算过 hashCode，就再也无法进入偏向状态了
- 添加 VM 参数 `-XX:-UseBiasedLocking` 禁用偏向锁

撤销偏向锁的状态：

- 调用对象的 hashCode：偏向锁的对象 MarkWord 中存储的是线程 id，调用 hashCode 导致偏向锁被撤销
- 当有其它线程使用偏向锁对象时，会将偏向锁升级为轻量级锁
- 调用 wait/notify，需要申请 Monitor，进入 WaitSet

**批量撤销**：如果对象被多个线程访问，但没有竞争，这时偏向了线程 T1 的对象仍有机会重新偏向 T2，重偏向会重置对象的 Thread ID

- 批量重偏向：当撤销偏向锁阈值超过 20 次后，JVM 会觉得是不是偏向错了，于是在给这些对象加锁时重新偏向至加锁线程
- 批量撤销：当撤销偏向锁阈值超过 40 次后，JVM 会觉得自己确实偏向错了，根本就不该偏向，于是整个类的所有对象都会变为不可偏向的，新建的对象也是不可偏向的

------

##### 锁消除

锁消除是指对于被检测出不可能存在竞争的共享数据的锁进行消除，这是 JVM **即时编译器的优化**

锁消除主要是通过**逃逸分析**来支持，如果堆上的共享数据不可能逃逸出去被其它线程访问到，那么就可以把它们当成私有数据对待，也就可以将它们的锁进行消除（同步消除：JVM 逃逸分析）

------

##### 锁粗化

对相同对象多次加锁，导致线程发生多次重入，频繁的加锁操作就会导致性能损耗，可以使用锁粗化方式优化

如果虚拟机探测到一串的操作都对同一个对象加锁，将会把加锁的范围扩展（粗化）到整个操作序列的外部

- 一些看起来没有加锁的代码，其实隐式的加了很多锁：

  ```java
  public static String concatString(String s1, String s2, String s3) {
      return s1 + s2 + s3;
  }
  ```

- String 是一个不可变的类，编译器会对 String 的拼接自动优化。在 JDK 1.5 之前，转化为 StringBuffer 对象的连续 append() 操作，每个 append() 方法中都有一个同步块

  ```java
  public static String concatString(String s1, String s2, String s3) {
      StringBuffer sb = new StringBuffer();
      sb.append(s1);
      sb.append(s2);
      sb.append(s3);
      return sb.toString();
  }
  ```

扩展到第一个 append() 操作之前直至最后一个 append() 操作之后，只需要加锁一次就可以

------

#### 多把锁

多把不相干的锁：一间大屋子有两个功能睡觉、学习，互不相干。现在一人要学习，一人要睡觉，如果只用一间屋子（一个对象锁）的话，那么并发度很低

将锁的粒度细分：

- 好处，是可以增强并发度
- 坏处，如果一个线程需要同时获得多把锁，就容易发生死锁

解决方法：准备多个对象锁

```java
public static void main(String[] args) {
    BigRoom bigRoom = new BigRoom();
    new Thread(() -> { bigRoom.study(); }).start();
    new Thread(() -> { bigRoom.sleep(); }).start();
}
class BigRoom {
    private final Object studyRoom = new Object();
    private final Object sleepRoom = new Object();

    public void sleep() throws InterruptedException {
        synchronized (sleepRoom) {
            System.out.println("sleeping 2 小时");
            Thread.sleep(2000);
        }
    }

    public void study() throws InterruptedException {
        synchronized (studyRoom) {
            System.out.println("study 1 小时");
            Thread.sleep(1000);
        }
    }
}
```

-----

#### 活跃性

##### 死锁

###### 形成

死锁：多个线程同时被阻塞，它们中的一个或者全部都在等待某个资源被释放，由于线程被无限期地阻塞，因此程序不可能正常终止

Java 死锁产生的四个必要条件：

1. 互斥条件，即当资源被一个线程使用（占有）时，别的线程不能使用
2. 不可剥夺条件，资源请求者不能强制从资源占有者手中夺取资源，资源只能由资源占有者主动释放
3. 请求和保持条件，即当资源请求者在请求其他的资源的同时保持对原有资源的占有
4. 循环等待条件，即存在一个等待循环队列：p1 要 p2 的资源，p2 要 p1 的资源，形成了一个等待环路

四个条件都成立的时候，便形成死锁。死锁情况下打破上述任何一个条件，便可让死锁消失

```java
public class Dead {
    public static Object resources1 = new Object();
    public static Object resources2 = new Object();
    public static void main(String[] args) {
        new Thread(() -> {
            // 线程1：占用资源1 ，请求资源2
            synchronized(resources1){
                System.out.println("线程1已经占用了资源1，开始请求资源2");
                Thread.sleep(2000);//休息两秒，防止线程1直接运行完成。
                //2秒内线程2肯定可以锁住资源2
                synchronized (resources2){
                    System.out.println("线程1已经占用了资源2");
                }
        }).start();
        new Thread(() -> {
            // 线程2：占用资源2 ，请求资源1
            synchronized(resources2){
                System.out.println("线程2已经占用了资源2，开始请求资源1");
                Thread.sleep(2000);
                synchronized (resources1){
                    System.out.println("线程2已经占用了资源1");
                }
            }}
        }).start();
    }
}
```

------

###### 定位

定位死锁的方法：

- 使用 jps 定位进程 id，再用 `jstack id` 定位死锁，找到死锁的线程去查看源码，解决优化

  ```java
  "Thread-1" #12 prio=5 os_prio=0 tid=0x000000001eb69000 nid=0xd40 waiting formonitor entry [0x000000001f54f000]
  	java.lang.Thread.State: BLOCKED (on object monitor)
  #省略    
  "Thread-1" #12 prio=5 os_prio=0 tid=0x000000001eb69000 nid=0xd40 waiting for monitor entry [0x000000001f54f000]
  	java.lang.Thread.State: BLOCKED (on object monitor)
  #省略
  
  Found one Java-level deadlock:
  ===================================================
  "Thread-1":
      waiting to lock monitor 0x000000000361d378 (object 0x000000076b5bf1c0, a java.lang.Object),
      which is held by "Thread-0"
  "Thread-0":
      waiting to lock monitor 0x000000000361e768 (object 0x000000076b5bf1d0, a java.lang.Object),
      which is held by "Thread-1"
      
  Java stack information for the threads listed above:
  ===================================================
  "Thread-1":
      at thread.TestDeadLock.lambda$main$1(TestDeadLock.java:28)
      - waiting to lock <0x000000076b5bf1c0> (a java.lang.Object)
      - locked <0x000000076b5bf1d0> (a java.lang.Object)
      at thread.TestDeadLock$$Lambda$2/883049899.run(Unknown Source)
      at java.lang.Thread.run(Thread.java:745)
  "Thread-0":
      at thread.TestDeadLock.lambda$main$0(TestDeadLock.java:15)
      - waiting to lock <0x000000076b5bf1d0> (a java.lang.Object)
      - locked <0x000000076b5bf1c0> (a java.lang.Object)
      at thread.TestDeadLock$$Lambda$1/495053715
  ```

- Linux 下可以通过 top 先定位到 CPU 占用高的 Java 进程，再利用 `top -Hp 进程id` 来定位是哪个线程，最后再用 jstack 的输出来看各个线程栈

- 避免死锁：避免死锁要注意加锁顺序

- 可以使用 jconsole 工具，在 `jdk\bin` 目录下

------

##### 活锁

活锁：指的是任务或者执行者没有被阻塞，由于某些条件没有满足，导致一直重复尝试—失败—尝试—失败的过程

两个线程互相改变对方的结束条件，最后谁也无法结束：

```java
class TestLiveLock {
    static volatile int count = 10;
    static final Object lock = new Object();
    public static void main(String[] args) {
        new Thread(() -> {
            // 期望减到 0 退出循环
            while (count > 0) {
                Thread.sleep(200);
                count--;
                System.out.println("线程一count:" + count);
            }
        }, "t1").start();
        new Thread(() -> {
            // 期望超过 20 退出循环
            while (count < 20) {
                Thread.sleep(200);
                count++;
                System.out.println("线程二count:"+ count);
            }
        }, "t2").start();
    }
}
```

------

##### 饥饿

饥饿：一个线程由于优先级太低，始终得不到 CPU 调度执行，也不能够结束

------

### ReentrantLock

相对于 synchronized 它具备如下特点

* 可中断
* 可以设置超时时间
* 可以设置为公平锁
* 支持多个条件变量

与 synchronized 一样，都支持可重入

基本语法

```java
// 获取锁
reentrantLock.lock();
try{
    // 临界区
} finally {
    // 释放锁
    reentrantLock.unlock();
}
```

#### 可重入

可重入是指同一个线程如果首次获得这把锁，那么因为它是这把锁的拥有者，因此有权利再次获取这把锁

如果是不可重入锁，那么第二次获得锁时，自己也会被锁挡住

#### 可打断

#### 锁超时

#### 公平锁

ReentrantLock 默认是不公平锁

也可以更改为公平锁

```java
RenntrantLock lock = new ReentrantLock(true);
```

公平锁一般没有必要，会降低并发度

#### 条件变量

Synchronized 中也有条件变量，就是我们讲原理时那个 waitSet 休息室，当条件不满足时进入 waitSet 等待

ReentrantLock 的条件变量比 synchronized 强大之处在于，它是支持多个条件变量的，这就好比

* synchronized 是那些不满足条件的线程都在一间休息室等消息
* 而 ReentrantLock 支持多间休息室，有专门的等烟休息室，专门等早餐的休息室、唤醒时也是按休息室来唤醒

使用流程：

* await 前需要获得锁
* await 执行后，会释放锁，进入 conditionObject 等待
* await 的线程被唤醒（或打断、或超时）取重新竞争 lock 锁
* 竞争 lock 锁成功后，从 await 后继续执行

------

### wait-ify

#### 基本使用

需要获取对象锁后才可以调用 `锁对象.wait()`，notify 随机唤醒一个线程，notifyAll 唤醒所有线程去竞争 CPU

Object 类 API：

```java
public final void notify():唤醒正在等待对象监视器的单个线程。
public final void notifyAll():唤醒正在等待对象监视器的所有线程。
public final void wait():导致当前线程等待，直到另一个线程调用该对象的notify()方法或 notifyAll()方法。
public final native void wait(long timeout):有时限的等待, 到n毫秒后结束等待，或是被唤醒
```

说明：**wait 是挂起线程，需要唤醒的都是挂起操作**，阻塞线程可以自己去争抢锁，挂起的线程需要唤醒后去争抢锁

对比 sleep()：

- 原理不同：sleep() 方法是属于 Thread 类，是线程用来控制自身流程的，使此线程暂停执行一段时间而把执行机会让给其他线程；wait() 方法属于 Object 类，用于线程间通信
- 对**锁的处理机制**不同：调用 sleep() 方法的过程中，线程不会释放对象锁，当调用 wait() 方法的时候，线程会放弃对象锁，进入等待此对象的等待锁定池（不释放锁其他线程怎么抢占到锁执行唤醒操作），但是都会释放 CPU
- 使用区域不同：wait() 方法必须放在**同步控制方法和同步代码块（先获取锁）**中使用，sleep() 方法则可以放在任何地方使用

底层原理：

- Owner 线程发现条件不满足，调用 wait 方法，即可进入 WaitSet 变为 WAITING 状态
- BLOCKED 和 WAITING 的线程都处于阻塞状态，不占用 CPU 时间片
- BLOCKED 线程会在 Owner 线程释放锁时唤醒
- WAITING 线程会在 Owner 线程调用 notify 或 notifyAll 时唤醒，唤醒后并不意味者立刻获得锁，**需要进入 EntryList 重新竞争**

![image-20221217143514287](JUC.assets/image-20221217143514287.png)

#### 代码优化

虚假唤醒：notify 只能随机唤醒一个 WaitSet 中的线程，这时如果有其它线程也在等待，那么就可能唤醒不了正确的线程

解决方法：采用 notifyAll

notifyAll 仅解决某个线程的唤醒问题，使用 if + wait 判断仅有一次机会，一旦条件不成立，无法重新判断

解决方法：用 while + wait，当条件不成立，再次 wait

```java
@Slf4j(topic = "c.demo")
public class demo {
    static final Object room = new Object();
    static boolean hasCigarette = false;    //有没有烟
    static boolean hasTakeout = false;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            synchronized (room) {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {//while防止虚假唤醒
                    log.debug("没烟，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (room) {
                Thread thread = Thread.currentThread();
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        room.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到没？[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();


        Thread.sleep(1000);
        new Thread(() -> {
        // 这里能不能加 synchronized (room)？
            synchronized (room) {
                hasTakeout = true;
				//log.debug("烟到了噢！");
                log.debug("外卖到了噢！");
                room.notifyAll();
            }
        }, "送外卖的").start();
    }
}
```

#### 交替输出 wait-notify

输出：abcabcabcabcabc

```java
/*
	输出内容	等待标记	下一个标记
	a		   1		  2
	b		   2		  3
	c		   3		  1
*/
class WaitNotify{
    // 等待标记
    private int flag;
    
    // 循环次数
    private int loopNumber;
    
    public WaitNotify(int flag, int loopNumber){
        this.flag = flag;
        this.loopNumber = loopNumber;
    }
    
    public void print(String str, int waitFlag, int nextFlag){
        for (int i = 0; i < loopNumber, i++){
            synchronized (this){
                while(flag != waitFlag) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(str);
                flag = nextFlag;
                this.notifyAll();
            }
        }
    }
}

// 测试代码
public static void main(String[] args){
    WaitNotify wn = new WaitNotify(1, 5);
    new Thread(() -> {
        wn.print("a", 1, 2);
    }).start();
    
    new Thread(() -> {
        wn.print("b", 2, 3);
    }).start();
    
    new Thread(() -> {
        wn.print("c", 3, 1);
    }).start();
}
```

#### 交替输出 await-signal

```java
public class Main {
    public static void Main(String[] args) {
        AwaitSignal awaitSignal = new AwaitSignal(5);
        Condition a = awaitSignal.newCondition();
        Condition b = awaitSignal.newCondition();
        Condition c = awaitSignal.newCondition();
        new Thread(() -> {
            awaitSignal.print("a", a, b);
        }).start();
        new Thread(() -> {
            awaitSignal.print("b", b, c);
        }).start();
        new Thread(() -> {
            awaitSignal.print("c", c, a);
        }).start();
        
        Thread.sleep(1000);
        awaitSignal.lock();
        try {
            System.out.println("开始...");
            a.signal();
        } finally {
            awaitSignal.unlock();
        }
    }
}

class AwaitSignal extends ReentrantLock{
    private int loopNumber;
    
    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }
    
    public void print(String str, Condition current, Condition next){
        for (int i = 0; i < loopNumber; i++){
            lock();
            try{
                current.await();
                System.out.print(str);
                next.signal();
            } catch (InterruptedException e){
                e.printStackTrace();
            } finally {
                unlock();
            }
        }
    }
}
```

------

### park-un

LockSupport 是用来创建锁和其他同步类的**线程原语**

LockSupport 类方法：

- `LockSupport.park()`：暂停当前线程，挂起原语
- `LockSupport.unpark(暂停的线程对象)`：恢复某个线程的运行

```java
public static void main(String[] args) {
    Thread t1 = new Thread(() -> {
        System.out.println("start...");	//1
		Thread.sleep(1000);// Thread.sleep(3000)
        // 先 park 再 unpark 和先 unpark 再 park 效果一样，都会直接恢复线程的运行
        System.out.println("park...");	//2
        LockSupport.park();
        System.out.println("resume...");//4
    },"t1");
    t1.start();
   	Thread.sleep(2000);
    System.out.println("unpark...");	//3
    LockSupport.unpark(t1);
}
```

LockSupport 出现就是为了增强 wait & notify 的功能：

- wait，notify 和 notifyAll 必须配合 Object Monitor 一起使用，而 park、unpark 不需要
- park & unpark **以线程为单位**来阻塞和唤醒线程，而 notify 只能随机唤醒一个等待线程，notifyAll 是唤醒所有等待线程
- park & unpark 可以先 unpark，而 wait & notify 不能先 notify。类比生产消费，先消费发现有产品就消费，没有就等待；先生产就直接产生商品，然后线程直接消费
- wait 会释放锁资源进入等待队列，**park 不会释放锁资源**，只负责阻塞当前线程，会释放 CPU

原理：类似生产者消费者

- 先 park：
  1. 当前线程调用 Unsafe.park() 方法
  2. 检查 _counter ，本情况为 0，这时获得 _mutex 互斥锁
  3. 线程进入 _cond 条件变量挂起
  4. 调用 Unsafe.unpark(Thread_0) 方法，设置 _counter 为 1
  5. 唤醒 _cond 条件变量中的 Thread_0，Thread_0 恢复运行，设置 _counter 为 0

![image-20221217202630060](JUC.assets/image-20221217202630060.png)

* 先 unpark：
  1. 调用 Unsafe.unpark(Thread_0) 方法，设置 _counter 为 1
  2. 当前线程调用 Unsafe.park() 方法
  3. 检查 _counter ，本情况为 1，这时线程无需挂起，继续运行，设置 _counter 为 0

![image-20221217202813772](JUC.assets/image-20221217202813772.png)

------

#### 交替输出 park-unPark

```java
public class Main{
    static Thread t1;
    static Thread t2;
    static Thread t3;
    
    public static void main(String[] args){
        ParkUnpark pu = new ParkUnpark(5);
        t1 = new Thread(() -> {
            pu.print("a", t2);
        });
        t2 = new Thread(() -> {
            pu.print("b", t3);
        });
        t3 = new Thread(() -> {
            pu.print("c", t1);
        });
        t1.start();
        t2.start();
        t3.start();
        
        LockSupport.unpark(t1);
    }
}

class ParkUnpark{
    private int loopNumber;
    
    public ParkUnpark(int loopNumber) {
        this.loopNumber = loopNumber;
    }
    
    public void print(String str, Thread next){
        for (int i = 0; i < loopNumber; i++){
            LockSupport.park();
            System.out.print(str);
            LockSupport.unpark(next);
        }
    }
}
```

------

### 安全分析

成员变量和静态变量：

- 如果它们没有共享，则线程安全
- 如果它们被共享了，根据它们的状态是否能够改变，分两种情况：
  - 如果只有读操作，则线程安全
  - 如果有读写操作，则这段代码是临界区，需要考虑线程安全问题

局部变量：

- 局部变量是线程安全的
- 局部变量引用的对象不一定线程安全（逃逸分析）：
  - 如果该对象没有逃离方法的作用访问，它是线程安全的（每一个方法有一个栈帧）
  - 如果该对象逃离方法的作用范围，需要考虑线程安全问题（暴露引用）

常见线程安全类：String、Integer、StringBuffer、Random、Vector、Hashtable、java.util.concurrent 包

- 线程安全的是指，多个线程调用它们同一个实例的某个方法时，是线程安全的

- **每个方法是原子的，但多个方法的组合不是原子的**，只能保证调用的方法内部安全：

  ```java
  Hashtable table = new Hashtable();
  // 线程1，线程2
  if(table.get("key") == null) {
  	table.put("key", value);
  }
  ```

无状态类线程安全，就是没有成员变量的类

不可变类线程安全：String、Integer 等都是不可变类，**内部的状态不可以改变**，所以方法是线程安全

- replace 等方法底层是新建一个对象，复制过去

  ```java
  Map<String,Object> map = new HashMap<>();	// 线程不安全
  String S1 = "...";							// 线程安全
  final String S2 = "...";					// 线程安全
  Date D1 = new Date();						// 线程不安全
  final Date D2 = new Date();					// 线程不安全，final让D2引用的对象不能变，但对象的内容可以变
  ```

抽象方法如果有参数，被重写后行为不确定可能造成线程不安全，被称之为外星方法：`public abstract foo(Student s);`

------

### 同步模式

#### 保护性暂停

##### 单任务版

Guarded Suspension，用在一个线程等待另一个线程的执行结果

- 有一个结果需要从一个线程传递到另一个线程，让它们关联同一个 GuardedObject
- 如果有结果不断从一个线程到另一个线程那么可以使用消息队列（见生产者/消费者）
- JDK 中，join 的实现、Future 的实现，采用的就是此模式

![image-20221217203116495](JUC.assets/image-20221217203116495.png)

```java
public static void main(String[] args) {
    GuardedObject object = new GuardedObjectV2();
    new Thread(() -> {
        sleep(1);
        object.complete(Arrays.asList("a", "b", "c"));
    }).start();
    
    Object response = object.get(2500);
    if (response != null) {
        log.debug("get response: [{}] lines", ((List<String>) response).size());
    } else {
        log.debug("can't get response");
    }
}

class GuardedObject {
    private Object response;
    private final Object lock = new Object();

    //获取结果
    //timeout :最大等待时间
    public Object get(long millis) {
        synchronized (lock) {
            // 1) 记录最初时间
            long begin = System.currentTimeMillis();
            // 2) 已经经历的时间
            long timePassed = 0;
            while (response == null) {
                // 4) 假设 millis 是 1000，结果在 400 时唤醒了，那么还有 600 要等
                long waitTime = millis - timePassed;
                log.debug("waitTime: {}", waitTime);
                //经历时间超过最大等待时间退出循环
                if (waitTime <= 0) {
                    log.debug("break...");
                    break;
                }
                try {
                    lock.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 3) 如果提前被唤醒，这时已经经历的时间假设为 400
                timePassed = System.currentTimeMillis() - begin;
                log.debug("timePassed: {}, object is null {}",
                        timePassed, response == null);
            }
            return response;
        }
    }

    //产生结果
    public void complete(Object response) {
        synchronized (lock) {
            // 条件满足，通知等待线程
            this.response = response;
            log.debug("notify...");
            lock.notifyAll();
        }
    }
}
```

##### 多任务版

多任务版保护性暂停：

```java
public static void main(String[] args) throws InterruptedException {
    for (int i = 0; i < 3; i++) {
        new People().start();
    }
    Thread.sleep(1000);
    for (Integer id : Mailboxes.getIds()) {
        new Postman(id, id + "号快递到了").start();
    }
}

@Slf4j(topic = "c.People")
class People extends Thread{
    @Override
    public void run() {
        // 收信
        GuardedObject guardedObject = Mailboxes.createGuardedObject();
        log.debug("开始收信i d:{}", guardedObject.getId());
        Object mail = guardedObject.get(5000);
        log.debug("收到信id:{}，内容:{}", guardedObject.getId(),mail);
    }
}

class Postman extends Thread{
    private int id;
    private String mail;
    //构造方法
    @Override
    public void run() {
        GuardedObject guardedObject = Mailboxes.getGuardedObject(id);
        log.debug("开始送信i d:{}，内容:{}", guardedObject.getId(),mail);
        guardedObject.complete(mail);
    }
}

class  Mailboxes {
    private static Map<Integer, GuardedObject> boxes = new Hashtable<>();
    private static int id = 1;

    //产生唯一的id
    private static synchronized int generateId() {
        return id++;
    }

    public static GuardedObject getGuardedObject(int id) {
        return boxes.remove(id);
    }

    public static GuardedObject createGuardedObject() {
        GuardedObject go = new GuardedObject(generateId());
        boxes.put(go.getId(), go);
        return go;
    }

    public static Set<Integer> getIds() {
        return boxes.keySet();
    }
}
class GuardedObject {
    //标识，Guarded Object
    private int id;//添加get set方法
}
```

#### 顺序输出

顺序输出 2 1

```java
public static void main(String[] args) throws InterruptedException {
    Thread t1 = new Thread(() -> {
        while (true) {
            //try { Thread.sleep(1000); } catch (InterruptedException e) { }
            // 当没有许可时，当前线程暂停运行；有许可时，用掉这个许可，当前线程恢复运行
            LockSupport.park();
            System.out.println("1");
        }
    });
    Thread t2 = new Thread(() -> {
        while (true) {
            System.out.println("2");
            // 给线程 t1 发放『许可』（多次连续调用 unpark 只会发放一个『许可』）
            LockSupport.unpark(t1);
            try { Thread.sleep(500); } catch (InterruptedException e) { }
        }
    });
    t1.start();
    t2.start();
}
```

------

#### 交替输出

连续输出 5 次 abc

```java
public class day2_14 {
    public static void main(String[] args) throws InterruptedException {
        AwaitSignal awaitSignal = new AwaitSignal(5);
        Condition a = awaitSignal.newCondition();
        Condition b = awaitSignal.newCondition();
        Condition c = awaitSignal.newCondition();
        new Thread(() -> {
            awaitSignal.print("a", a, b);
        }).start();
        new Thread(() -> {
            awaitSignal.print("b", b, c);
        }).start();
        new Thread(() -> {
            awaitSignal.print("c", c, a);
        }).start();

        Thread.sleep(1000);
        awaitSignal.lock();
        try {
            a.signal();
        } finally {
            awaitSignal.unlock();
        }
    }
}

class AwaitSignal extends ReentrantLock {
    private int loopNumber;

    public AwaitSignal(int loopNumber) {
        this.loopNumber = loopNumber;
    }
    //参数1：打印内容  参数二：条件变量  参数二：唤醒下一个
    public void print(String str, Condition condition, Condition next) {
        for (int i = 0; i < loopNumber; i++) {
            lock();
            try {
                condition.await();
                System.out.print(str);
                next.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                unlock();
            }
        }
    }
}
```

------

### 异步模式

#### 传统版

异步模式之生产者/消费者：

```java
class ShareData {
    private int number = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment() throws Exception{
        // 同步代码块，加锁
        lock.lock();
        try {
            // 判断  防止虚假唤醒
            while(number != 0) {
                // 等待不能生产
                condition.await();
            }
            // 干活
            number++;
            System.out.println(Thread.currentThread().getName() + "\t " + number);
            // 通知 唤醒
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void decrement() throws Exception{
        // 同步代码块，加锁
        lock.lock();
        try {
            // 判断 防止虚假唤醒
            while(number == 0) {
                // 等待不能消费
                condition.await();
            }
            // 干活
            number--;
            System.out.println(Thread.currentThread().getName() + "\t " + number);
            // 通知 唤醒
            condition.signalAll();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}

public class TraditionalProducerConsumer {
	public static void main(String[] args) {
        ShareData shareData = new ShareData();
        // t1线程，生产
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
            	shareData.increment();
            }
        }, "t1").start();

        // t2线程，消费
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
				shareData.decrement();
            }
        }, "t2").start(); 
    }
}
```

#### 改进版

异步模式之生产者/消费者：

- 消费队列可以用来平衡生产和消费的线程资源，不需要产生结果和消费结果的线程一一对应
- 生产者仅负责产生结果数据，不关心数据该如何处理，而消费者专心处理结果数据
- 消息队列是有容量限制的，满时不会再加入数据，空时不会再消耗数据
- JDK 中各种阻塞队列，采用的就是这种模式

![image-20221217203234588](JUC.assets/image-20221217203234588.png)

```java
public class demo {
    public static void main(String[] args) {
        MessageQueue queue = new MessageQueue(2);
        for (int i = 0; i < 3; i++) {
            int id = i;
            new Thread(() -> {
                queue.put(new Message(id,"值"+id));
            }, "生产者" + i).start();
        }
        
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message message = queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"消费者").start();
    }
}

//消息队列类，Java间线程之间通信
class MessageQueue {
    private LinkedList<Message> list = new LinkedList<>();//消息的队列集合
    private int capacity;//队列容量
    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    //获取消息
    public Message take() {
        //检查队列是否为空
        synchronized (list) {
            while (list.isEmpty()) {
                try {
                    sout(Thread.currentThread().getName() + ":队列为空，消费者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //从队列的头部获取消息返回
            Message message = list.removeFirst();
            sout(Thread.currentThread().getName() + "：已消费消息--" + message);
            list.notifyAll();
            return message;
        }
    }

    //存入消息
    public void put(Message message) {
        synchronized (list) {
            //检查队列是否满
            while (list.size() == capacity) {
                try {
                    sout(Thread.currentThread().getName()+":队列为已满，生产者线程等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //将消息加入队列尾部
            list.addLast(message);
            sout(Thread.currentThread().getName() + ":已生产消息--" + message);
            list.notifyAll();
        }
    }
}

final class Message {
    private int id;
    private Object value;
	//get set
}
```

#### 阻塞队列

```java
public static void main(String[] args) {
    ExecutorService consumer = Executors.newFixedThreadPool(1);
    ExecutorService producer = Executors.newFixedThreadPool(1);
    BlockingQueue<Integer> queue = new SynchronousQueue<>();
    producer.submit(() -> {
        try {
            System.out.println("生产...");
            Thread.sleep(1000);
            queue.put(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    consumer.submit(() -> {
        try {
            System.out.println("等待消费...");
            Integer result = queue.take();
            System.out.println("结果为:" + result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
}
```

------

## 共享模型之内存

## 内存

### JMM

#### 内存模型

Java 内存模型是 Java Memory Model（JMM），本身是一种**抽象的概念**，实际上并不存在，描述的是一组规则或规范，通过这组规范定义了程序中各个变量（包括实例字段，静态字段和构成数组对象的元素）的访问方式

JMM 作用：

- 屏蔽各种硬件和操作系统的内存访问差异，实现让 Java 程序在各种平台下都能达到一致的内存访问效果
- 规定了线程和内存之间的一些关系

根据 JMM 的设计，系统存在一个主内存（Main Memory），Java 中所有变量都存储在主存中，对于所有线程都是共享的；每条线程都有自己的工作内存（Working Memory），工作内存中保存的是主存中某些**变量的拷贝**，线程对所有变量的操作都是先对变量进行拷贝，然后在工作内存中进行，不能直接操作主内存中的变量；线程之间无法相互直接访问，线程间的通信（传递）必须通过主内存来完成

<img src="JUC.assets/image-20221217205301303.png" alt="image-20221217205301303" style="zoom:50%;" />

主内存和工作内存：

- 主内存：计算机的内存，也就是经常提到的 8G 内存，16G 内存，存储所有共享变量的值
- 工作内存：存储该线程使用到的共享变量在主内存的的值的副本拷贝

**JVM 和 JMM 之间的关系**：JMM 中的主内存、工作内存与 JVM 中的 Java 堆、栈、方法区等并不是同一个层次的内存划分，这两者基本上是没有关系的，如果两者一定要勉强对应起来：

- 主内存主要对应于 Java 堆中的对象实例数据部分，而工作内存则对应于虚拟机栈中的部分区域
- 从更低层次上说，主内存直接对应于物理硬件的内存，工作内存对应寄存器和高速缓存

------

#### 内存交互

Java 内存模型定义了 8 个操作来完成主内存和工作内存的交互操作，每个操作都是**原子**的

非原子协定：没有被 volatile 修饰的 long、double 外，默认按照两次 32 位的操作

<img src="JUC.assets/image-20221217205327400.png" alt="image-20221217205327400" style="zoom:80%;" />

- lock：作用于主内存，将一个变量标识为被一个线程独占状态（对应 monitorenter）
- unclock：作用于主内存，将一个变量从独占状态释放出来，释放后的变量才可以被其他线程锁定（对应 monitorexit）
- read：作用于主内存，把一个变量的值从主内存传输到工作内存中
- load：作用于工作内存，在 read 之后执行，把 read 得到的值放入工作内存的变量副本中
- use：作用于工作内存，把工作内存中一个变量的值传递给**执行引擎**，每当遇到一个使用到变量的操作时都要使用该指令
- assign：作用于工作内存，把从执行引擎接收到的一个值赋给工作内存的变量
- store：作用于工作内存，把工作内存的一个变量的值传送到主内存中
- write：作用于主内存，在 store 之后执行，把 store 得到的值放入主内存的变量中

------

#### 三大特性

##### 可见性

可见性：是指当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值

存在不可见问题的根本原因是由于缓存的存在，线程持有的是共享变量的副本，无法感知其他线程对于共享变量的更改，导致读取的值不是最新的。但是 final 修饰的变量是**不可变**的，就算有缓存，也不会存在不可见的问题

main 线程对 run 变量的修改对于 t 线程不可见，导致了 t 线程无法停止：

```java
static boolean run = true;	//添加volatile
public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread(()->{
        while(run){
        // ....
        }
	});
    t.start();
    sleep(1);
    run = false; // 线程t不会如预想的停下来
}
```

原因：

- 初始状态， t 线程刚开始从主内存读取了 run 的值到工作内存
- 因为 t 线程要频繁从主内存中读取 run 的值，JIT 编译器会将 run 的值缓存至自己工作内存中的高速缓存中，减少对主存中 run 的访问，提高效率
- 1 秒之后，main 线程修改了 run 的值，并同步至主存，而 t 是从自己工作内存中的高速缓存中读取这个变量的值，结果永远是旧值

<img src="JUC.assets/image-20221217213134900.png" alt="image-20221217213134900" style="zoom:50%;" />

解决办法：

volatile（易变关键字）

它可以用来修饰成员变量和静态成员变量，它可以避免线程从自己的工作缓存中查找变量的值，必须到主内存中获取它的值，线程操作 volatile 变量都是直接操作主存

```java
// 不会从缓存中获取，而从主内存中拿到值
volatile static boolean run = true;	//添加volatile
public static void main(String[] args) throws InterruptedException {
    Thread t = new Thread(()->{
        while(run){
        // ....
        }
	});
    t.start();
    sleep(1);
    run = false; // 线程t不会如预想的停下来
}
```

------

##### 原子性

原子性：不可分割，完整性，也就是说某个线程正在做某个具体业务时，中间不可以被分割，需要具体完成，要么同时成功，要么同时失败，保证指令不会受到线程上下文切换的影响

定义原子操作的使用规则：

1. 不允许 read 和 load、store 和 write 操作之一单独出现，必须顺序执行，但是不要求连续
2. 不允许一个线程丢弃 assign 操作，必须同步回主存
3. 不允许一个线程无原因地（没有发生过任何 assign 操作）把数据从工作内存同步会主内存中
4. 一个新的变量只能在主内存中诞生，不允许在工作内存中直接使用一个未被初始化（assign 或者 load）的变量，即对一个变量实施 use 和 store 操作之前，必须先自行 assign 和 load 操作
5. 一个变量在同一时刻只允许一条线程对其进行 lock 操作，但 lock 操作可以被同一线程重复执行多次，多次执行 lock 后，只有**执行相同次数的 unlock** 操作，变量才会被解锁，**lock 和 unlock 必须成对出现**
6. 如果对一个变量执行 lock 操作，将会**清空工作内存中此变量的值**，在执行引擎使用这个变量之前需要重新从主存加载
7. 如果一个变量事先没有被 lock 操作锁定，则不允许执行 unlock 操作，也不允许去 unlock 一个被其他线程锁定的变量
8. 对一个变量执行 unlock 操作之前，必须**先把此变量同步到主内存**中（执行 store 和 write 操作）

> 注意： synchronized 语句块既可以保证代码块的原子性，也同时保证代码块内变量的可见性。但缺点是 synchronized 是属于重量级操作，性能相对更低。
>
> 如果在前面示例的死循环中加入 System.out.println() 会发现即使不加 volatile 修饰符，线程 t 也能正确看到对 run 变量的修改了，这是为什么？
>
> 因为 println() 底层也是用到了 synchronized 修饰

------

##### 有序性

有序性：在本线程内观察，所有操作都是有序的；在一个线程观察另一个线程，所有操作都是无序的，无序是因为发生了指令重排序

CPU 的基本工作是执行存储的指令序列，即程序，程序的执行过程实际上是不断地取出指令、分析指令、执行指令的过程，为了提高性能，编译器和处理器会对指令重排，一般分为以下三种：

```
源代码 -> 编译器优化的重排 -> 指令并行的重排 -> 内存系统的重排 -> 最终执行指令
```

现代 CPU 支持多级指令流水线，几乎所有的冯•诺伊曼型计算机的 CPU，其工作都可以分为 5 个阶段：取指令、指令译码、执行指令、访存取数和结果写回，可以称之为**五级指令流水线**。CPU 可以在一个时钟周期内，同时运行五条指令的**不同阶段**（每个线程不同的阶段），本质上流水线技术并不能缩短单条指令的执行时间，但变相地提高了指令地吞吐率

处理器在进行重排序时，必须要考虑**指令之间的数据依赖性**

- 单线程环境也存在指令重排，由于存在依赖性，最终执行结果和代码顺序的结果一致
- 多线程环境中线程交替执行，由于编译器优化重排，会获取其他线程处在不同阶段的指令同时执行

补充知识：

- 指令周期是取出一条指令并执行这条指令的时间，一般由若干个机器周期组成
- 机器周期也称为 CPU 周期，一条指令的执行过程划分为若干个阶段（如取指、译码、执行等），每一阶段完成一个基本操作，完成一个基本操作所需要的时间称为机器周期
- 振荡周期指周期性信号作周期性重复变化的时间间隔

------

### cache

#### 缓存机制

##### 缓存结构

在计算机系统中，CPU 高速缓存（CPU Cache，简称缓存）是用于减少处理器访问内存所需平均时间的部件；在存储体系中位于自顶向下的第二层，仅次于 CPU 寄存器；其容量远小于内存，但速度却可以接近处理器的频率

CPU 处理器速度远远大于在主内存中的，为了解决速度差异，在它们之间架设了多级缓存，如 L1、L2、L3 级别的缓存，这些缓存离 CPU 越近就越快，将频繁操作的数据缓存到这里，加快访问速度

![image-20221218161331698](JUC.assets/image-20221218161331698.png)

| 从 CPU 到 | 大约需要的时钟周期                |
| --------- | --------------------------------- |
| 寄存器    | 1 cycle (4GHz 的 CPU 约为 0.25ns) |
| L1        | 3~4 cycle                         |
| L2        | 10~20 cycle                       |
| L3        | 40~45 cycle                       |
| 内存      | 120~240 cycle                     |

##### 缓存使用

当处理器发出内存访问请求时，会先查看缓存内是否有请求数据，如果存在（命中），则不用访问内存直接返回该数据；如果不存在（失效），则要先把内存中的相应数据载入缓存，再将其返回处理器

缓存之所以有效，主要因为程序运行时对内存的访问呈现局部性（Locality）特征。既包括空间局部性（Spatial Locality），也包括时间局部性（Temporal Locality），有效利用这种局部性，缓存可以达到极高的命中率

------

#### 伪共享

**缓存以缓存行 cache line 为单位**，每个缓存行对应着一块内存，一般是 64 byte（8 个 long），在 CPU 从主存获取数据时，以 cache line 为单位加载，于是相邻的数据会一并加载到缓存中

缓存会造成数据副本的产生，即同一份数据会缓存在不同核心的缓存行中，CPU 要保证数据的一致性，需要做到某个 CPU 核心更改了数据，其它 CPU 核心对应的**整个缓存行必须失效**，这就是伪共享

![image-20221218161408132](JUC.assets/image-20221218161408132.png)

解决方法：

- padding：通过填充，让数据落在不同的 cache line 中
- @Contended：原理参考 无锁 → Adder → 优化机制 → 伪共享

Linux 查看 CPU 缓存行：

- 命令：`cat /sys/devices/system/cpu/cpu0/cache/index0/coherency_line_size64`
- 内存地址格式：[高位组标记] [低位索引] [偏移量]

------

#### 缓存一致

缓存一致性：当多个处理器运算任务都涉及到同一块主内存区域的时候，将可能导致各自的缓存数据不一样

![image-20221218161426114](JUC.assets/image-20221218161426114.png)

MESI（Modified Exclusive Shared Or Invalid）是一种广泛使用的**支持写回策略的缓存一致性协议**，CPU 中每个缓存行（caceh line）使用 4 种状态进行标记（使用额外的两位 bit 表示)：

- M：被修改（Modified）

  该缓存行只被缓存在该 CPU 的缓存中，并且是被修改过的，与主存中的数据不一致 (dirty)，该缓存行中的内存需要写回 (write back) 主存。该状态的数据再次被修改不会发送广播，因为其他核心的数据已经在第一次修改时失效一次

  当被写回主存之后，该缓存行的状态会变成独享 (exclusive) 状态

- E：独享的（Exclusive）

  该缓存行只被缓存在该 CPU 的缓存中，是未被修改过的 (clear)，与主存中数据一致，修改数据不需要通知其他 CPU 核心，该状态可以在任何时刻有其它 CPU 读取该内存时变成共享状态 (shared)

  当 CPU 修改该缓存行中内容时，该状态可以变成 Modified 状态

- S：共享的（Shared）

  该状态意味着该缓存行可能被多个 CPU 缓存，并且各个缓存中的数据与主存数据一致，当 CPU 修改该缓存行中，会向其它 CPU 核心广播一个请求，使该缓存行变成无效状态 (Invalid)，然后再更新当前 Cache 里的数据

- I：无效的（Invalid）

  该缓存是无效的，可能有其它 CPU 修改了该缓存行

解决方法：各个处理器访问缓存时都遵循一些协议，在读写时要根据协议进行操作，协议主要有 MSI、MESI 等

------

#### 处理机制

单核 CPU 处理器会自动保证基本内存操作的原子性

多核 CPU 处理器，每个 CPU 处理器内维护了一块内存，每个内核内部维护着一块缓存，当多线程并发读写时，就会出现缓存数据不一致的情况。处理器提供：

- 总线锁定：当处理器要操作共享变量时，在 BUS 总线上发出一个 LOCK 信号，其他处理器就无法操作这个共享变量，该操作会导致大量阻塞，从而增加系统的性能开销（**平台级别的加锁**）
- 缓存锁定：当处理器对缓存中的共享变量进行了操作，其他处理器有嗅探机制，将各自缓存中的该共享变量的失效，读取时会重新从主内存中读取最新的数据，基于 MESI 缓存一致性协议来实现

有如下两种情况处理器不会使用缓存锁定：

- 当操作的数据跨多个缓存行，或没被缓存在处理器内部，则处理器会使用总线锁定
- 有些处理器不支持缓存锁定，比如：Intel 486 和 Pentium 处理器也会调用总线锁定

总线机制：

- 总线嗅探：每个处理器通过嗅探在总线上传播的数据来检查自己缓存值是否过期了，当处理器发现自己的缓存对应的内存地址的数据被修改，就**将当前处理器的缓存行设置为无效状态**，当处理器对这个数据进行操作时，会重新从内存中把数据读取到处理器缓存中
- 总线风暴：当某个 CPU 核心更新了 Cache 中的数据，要把该事件广播通知到其他核心（**写传播**），CPU 需要每时每刻监听总线上的一切活动，但是不管别的核心的 Cache 是否缓存相同的数据，都需要发出一个广播事件，不断的从主内存嗅探和 CAS 循环，无效的交互会导致总线带宽达到峰值；因此不要大量使用 volatile 关键字，使用 volatile、syschonized 都需要根据实际场景

------

### volatile

#### 同步机制

volatile 是 Java 虚拟机提供的**轻量级**的同步机制（三大特性）

- 保证可见性
- 不保证原子性
- 保证有序性（禁止指令重排）

性能：volatile 修饰的变量进行读操作与普通变量几乎没什么差别，但是写操作相对慢一些，因为需要在本地代码中插入很多内存屏障来保证指令不会发生乱序执行，但是开销比锁要小

synchronized 无法禁止指令重排和处理器优化，为什么可以保证有序性可见性

- 加了锁之后，只能有一个线程获得到了锁，获得不到锁的线程就要阻塞，所以同一时间只有一个线程执行，相当于单线程，由于数据依赖性的存在，单线程的指令重排是没有问题的
- 线程加锁前，将**清空工作内存**中共享变量的值，使用共享变量时需要从主内存中重新读取最新的值；线程解锁前，必须把共享变量的最新值**刷新到主内存**中（JMM 内存交互章节有讲）

------

#### 指令重排

volatile 修饰的变量，可以禁用指令重排

指令重排实例：

- example 1：

  ```java
  public void mySort() {
  	int x = 11;	//语句1
  	int y = 12;	//语句2  谁先执行效果一样
  	x = x + 5;	//语句3
  	y = x * x;	//语句4
  }
  ```

  执行顺序是：1 2 3 4、2 1 3 4、1 3 2 4

  指令重排也有限制不会出现：4321，语句 4 需要依赖于 y 以及 x 的申明，因为存在数据依赖，无法首先执行

- example 2：

  ```java
  int num = 0;
  boolean ready = false;
  // 线程1 执行此方法
  public void actor1(I_Result r) {
      if(ready) {
      	r.r1 = num + num;
      } else {
      	r.r1 = 1;
      }
  }
  // 线程2 执行此方法
  public void actor2(I_Result r) {
  	num = 2;
  	ready = true;
  }
  ```

  情况一：线程 1 先执行，ready = false，结果为 r.r1 = 1

  情况二：线程 2 先执行 num = 2，但还没执行 ready = true，线程 1 执行，结果为 r.r1 = 1

  情况三：线程 2 先执行 ready = true，线程 1 执行，进入 if 分支结果为 r.r1 = 4

  情况四：线程 2 执行 ready = true，切换到线程 1，进入 if 分支为 r.r1 = 0，再切回线程 2 执行 num = 2，发生指令重排

------

#### 底层原理

##### 缓存一致

使用 volatile 修饰的共享变量，总线会开启 **CPU 总线嗅探机制**来解决 JMM 缓存一致性问题，也就是共享变量在多线程中可见性的问题，实现 MESI 缓存一致性协议

底层是通过汇编 lock 前缀指令，共享变量加了 lock 前缀指令就会进行缓存锁定，在线程修改完共享变量后写回主存，其他的 CPU 核心上运行的线程根据总线嗅探机制会修改其共享变量为失效状态，读取时会重新从主内存中读取最新的数据

lock 前缀指令就相当于内存屏障，Memory Barrier（Memory Fence）

- 对 volatile 变量的写指令后会加入写屏障
- 对 volatile 变量的读指令前会加入读屏障

内存屏障有三个作用：

- 确保对内存的读-改-写操作原子执行
- 阻止屏障两侧的指令重排序
- 强制把缓存中的脏数据写回主内存，让缓存行中相应的数据失效

##### 内存屏障

保证**可见性**：

- 写屏障（sfence，Store Barrier）保证在该屏障之前的，对共享变量的改动，都同步到主存当中

  ```java
  public void actor2(I_Result r) {
      num = 2;
      ready = true; // ready 是 volatile 赋值带写屏障
      // 写屏障
  }
  ```

- 读屏障（lfence，Load Barrier）保证在该屏障之后的，对共享变量的读取，从主存刷新变量值，加载的是主存中最新数据

  ```java
  public void actor1(I_Result r) {
      // 读屏障
      // ready 是 volatile 读取值带读屏障
      if(ready) {
      	r.r1 = num + num;
      } else {
      	r.r1 = 1;
      }
  }
  ```

![image-20221218162855956](JUC.assets/image-20221218162855956.png)

- 全能屏障：mfence（modify/mix Barrier），兼具 sfence 和 lfence 的功能

保证**有序性**：

- 写屏障会确保指令重排序时，不会将写屏障之前的代码排在写屏障之后
- 读屏障会确保指令重排序时，不会将读屏障之后的代码排在读屏障之前

不能解决指令交错：

- 写屏障仅仅是保证之后的读能够读到最新的结果，但不能保证其他线程的读跑到写屏障之前

- 有序性的保证也只是保证了本线程内相关代码不被重排序

  ```java
  volatile i = 0;
  new Thread(() -> {i++});
  new Thread(() -> {i--});
  ```

  i++ 反编译后的指令：

  ```java
  0: iconst_1			// 当int取值 -1~5 时，JVM采用iconst指令将常量压入栈中
  1: istore_1			// 将操作数栈顶数据弹出，存入局部变量表的 slot 1
  2: iinc		1, 1	
  ```

![image-20221218162929625](JUC.assets/image-20221218162929625.png)

##### 交互规则

对于 volatile 修饰的变量：

- 线程对变量的 use 与 load、read 操作是相关联的，所以变量使用前必须先从主存加载
- 线程对变量的 assign 与 store、write 操作是相关联的，所以变量使用后必须同步至主存
- 线程 1 和线程 2 谁先对变量执行 read 操作，就会先进行 write 操作，防止指令重排

------

#### 双端检锁

##### 检锁机制

Double-Checked Locking：双端检锁机制

DCL（双端检锁）机制不一定是线程安全的，原因是有指令重排的存在，加入 volatile 可以禁止指令重排

```java
public final class Singleton {
    private Singleton() { }
    private static Singleton INSTANCE = null;
    
    public static Singleton getInstance() {
        if(INSTANCE == null) { // t2，这里的判断不是线程安全的
            // 首次访问会同步，而之后的使用没有 synchronized
            synchronized(Singleton.class) {
                // 这里是线程安全的判断，防止其他线程在当前线程等待锁的期间完成了初始化
                if (INSTANCE == null) { 
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }
}
```

不锁 INSTANCE 的原因：

- INSTANCE 要重新赋值
- INSTANCE 是 null，线程加锁之前需要获取对象的引用，设置对象头，null 没有引用

实现特点：

- 懒惰初始化
- 首次使用 getInstance() 才使用 synchronized 加锁，后续使用时无需加锁
- 第一个 if 使用了 INSTANCE 变量，是在同步块之外，但在多线程环境下会产生问题

##### DCL问题

getInstance 方法对应的字节码为：

```java
0: 	getstatic 		#2 		// Field INSTANCE:Ltest/Singleton;
3: 	ifnonnull 		37
6: 	ldc 			#3 		// class test/Singleton
8: 	dup
9: 	astore_0
10: monitorenter
11: getstatic 		#2 		// Field INSTANCE:Ltest/Singleton;
14: ifnonnull 27
17: new 			#3 		// class test/Singleton
20: dup
21: invokespecial 	#4 		// Method "<init>":()V
24: putstatic 		#2 		// Field INSTANCE:Ltest/Singleton;
27: aload_0
28: monitorexit
29: goto 37
32: astore_1
33: aload_0
34: monitorexit
35: aload_1
36: athrow
37: getstatic 		#2 		// Field INSTANCE:Ltest/Singleton;
40: areturn
```

- 17 表示创建对象，将对象引用入栈
- 20 表示复制一份对象引用，引用地址
- 21 表示利用一个对象引用，调用构造方法初始化对象
- 24 表示利用一个对象引用，赋值给 static INSTANCE

**步骤 21 和 24 之间不存在数据依赖关系**，而且无论重排前后，程序的执行结果在单线程中并没有改变，因此这种重排优化是允许的

- 关键在于 0:getstatic 这行代码在 monitor 控制之外，可以越过 monitor 读取 INSTANCE 变量的值
- 当其他线程访问 INSTANCE 不为 null 时，由于 INSTANCE 实例未必已初始化，那么 t2 拿到的是将是一个未初始化完毕的单例返回，这就造成了线程安全的问题

![image-20221219124713678](JUC.assets/image-20221219124713678.png)

##### 解决方法

指令重排只会保证串行语义的执行一致性（单线程），但并不会关系多线程间的语义一致性

引入 volatile，来保证出现指令重排的问题，从而保证单例模式的线程安全性：

```java
private static volatile SingletonDemo INSTANCE = null;
```

------

happens-before

Java 内存模型具备一些先天的“有序性”，即不需要通过任何同步手段（volatile、synchronized 等）就能够得到保证的安全，这个通常也称为 happens-before 原则，它是可见性与有序性的一套规则总结

不符合 happens-before 规则，JMM 并不能保证一个线程的可见性和有序性

1. 程序次序规则 (Program Order Rule)：一个线程内，逻辑上书写在前面的操作先行发生于书写在后面的操作 ，因为多个操作之间有先后依赖关系，则不允许对这些操作进行重排序

2. 锁定规则 (Monitor Lock Rule)：一个 unlock 操作先行发生于后面（时间的先后）对同一个锁的 lock 操作，所以线程解锁 m 之前对变量的写（解锁前会刷新到主内存中），对于接下来对 m 加锁的其它线程对该变量的读可见

3. **volatile 变量规则** (Volatile Variable Rule)：对 volatile 变量的写操作先行发生于后面对这个变量的读

4. 传递规则 (Transitivity)：具有传递性，如果操作 A 先行发生于操作 B，而操作 B 又先行发生于操作 C，则可以得出操作 A 先行发生于操作 C

5. 线程启动规则 (Thread Start Rule)：Thread 对象的 start()方 法先行发生于此线程中的每一个操作

   ```java
   static int x = 10;//线程 start 前对变量的写，对该线程开始后对该变量的读可见
   new Thread(()->{	System.out.println(x);	},"t1").start();
   ```

6. 线程中断规则 (Thread Interruption Rule)：对线程 interrupt() 方法的调用先行发生于被中断线程的代码检测到中断事件的发生

7. 线程终止规则 (Thread Termination Rule)：线程中所有的操作都先行发生于线程的终止检测，可以通过 Thread.join() 方法结束、Thread.isAlive() 的返回值手段检测到线程已经终止执行

8. 对象终结规则（Finaizer Rule）：一个对象的初始化完成（构造函数执行结束）先行发生于它的 finalize() 方法的开始

------

### 设计模式

#### 终止模式

终止模式之两阶段终止模式：停止标记用 volatile 是为了保证该变量在多个线程之间的可见性

```java
class TwoPhaseTermination {
    // 监控线程
    private Thread monitor;
    // 停止标记
    private volatile boolean stop = false;;

    // 启动监控线程
    public void start() {
        monitor = new Thread(() -> {
            while (true) {
                Thread thread = Thread.currentThread();
                if (stop) {
                    System.out.println("后置处理");
                    break;
                }
                try {
                    Thread.sleep(1000);// 睡眠
                    System.out.println(thread.getName() + "执行监控记录");
                } catch (InterruptedException e) {
                   	System.out.println("被打断，退出睡眠");
                }
            }
        });
        monitor.start();
    }

    // 停止监控线程
    public void stop() {
        stop = true;
        monitor.interrupt();// 让线程尽快退出Timed Waiting
    }
}
// 测试
public static void main(String[] args) throws InterruptedException {
    TwoPhaseTermination tpt = new TwoPhaseTermination();
    tpt.start();
    Thread.sleep(3500);
    System.out.println("停止监控");
    tpt.stop();
}
```

------

#### Balking

Balking （犹豫）模式用在一个线程发现另一个线程或本线程已经做了某一件相同的事，那么本线程就无需再做了，直接结束返回

```java
public class MonitorService {
    // 用来表示是否已经有线程已经在执行启动了
    private volatile boolean starting = false;
    public void start() {
        System.out.println("尝试启动监控线程...");
        synchronized (this) {
            if (starting) {
            	return;
            }
            starting = true;
        }
        // 真正启动监控线程...
    }
}
```

对比保护性暂停模式：保护性暂停模式用在一个线程等待另一个线程的执行结果，当条件不满足时线程等待

例子：希望 doInit() 方法仅被调用一次，下面的实现出现的问题：

- 当 t1 线程进入 init() 准备 doInit()，t2 线程进来，initialized 还为f alse，则 t2 就又初始化一次
- volatile 适合一个线程写，其他线程读的情况，这个代码需要加锁

```java
public class TestVolatile {
    volatile boolean initialized = false;
    
    void init() {
        if (initialized) {
            return;
        }
    	doInit();
    	initialized = true;
    }
    private void doInit() {
    }
}
```

------

## 无锁

### CAS

#### 原理

无锁编程：Lock Free

CAS 的全称是 Compare-And-Swap，是 **CPU 并发原语**

- CAS 并发原语体现在 Java 语言中就是 sun.misc.Unsafe 类的各个方法，调用 UnSafe 类中的 CAS 方法，JVM 会实现出 CAS 汇编指令，这是一种完全依赖于硬件的功能，实现了原子操作
- CAS 是一种系统原语，原语属于操作系统范畴，是由若干条指令组成 ，用于完成某个功能的一个过程，并且原语的执行必须是连续的，执行过程中不允许被中断，所以 CAS 是一条 CPU 的原子指令，不会造成数据不一致的问题，是线程安全的

底层原理：CAS 的底层是 `lock cmpxchg` 指令（X86 架构），在单核和多核 CPU 下都能够保证比较交换的原子性

- 程序是在单核处理器上运行，会省略 lock 前缀，单处理器自身会维护处理器内的顺序一致性，不需要 lock 前缀的内存屏障效果
- 程序是在多核处理器上运行，会为 cmpxchg 指令加上 lock 前缀。当某个核执行到带 lock 的指令时，CPU 会执行**总线锁定或缓存锁定**，将修改的变量写入到主存，这个过程不会被线程的调度机制所打断，保证了多个线程对内存操作的原子性

作用：比较当前工作内存中的值和主物理内存中的值，如果相同则执行规定操作，否则继续比较直到主内存和工作内存的值一致为止

CAS 特点：

- CAS 体现的是**无锁并发、无阻塞并发**，线程不会陷入阻塞，线程不需要频繁切换状态（上下文切换，系统调用）
- CAS 是基于乐观锁的思想

CAS 缺点：

- 执行的是循环操作，如果比较不成功一直在循环，最差的情况某个线程一直取到的值和预期值都不一样，就会无限循环导致饥饿，**使用 CAS 线程数不要超过 CPU 的核心数**，采用分段 CAS 和自动迁移机制
- 只能保证一个共享变量的原子操作
  - 对于一个共享变量执行操作时，可以通过循环 CAS 的方式来保证原子操作
  - 对于多个共享变量操作时，循环 CAS 就无法保证操作的原子性，这个时候**只能用锁来保证原子性**
- 引出来 ABA 问题

------

#### 乐观锁

CAS 与 synchronized 总结：

- synchronized 是从悲观的角度出发：总是假设最坏的情况，每次去拿数据的时候都认为别人会修改，所以每次在拿数据的时候都会上锁，这样别人想拿这个数据就会阻塞（共享资源每次只给一个线程使用，其它线程阻塞，用完后再把资源转让给其它线程），因此 synchronized 也称之为悲观锁，ReentrantLock 也是一种悲观锁，性能较差
- CAS 是从乐观的角度出发：总是假设最好的情况，每次去拿数据的时候都认为别人不会修改，所以不会上锁，但是在更新的时候会判断一下在此期间别人有没有去更新这个数据。**如果别人修改过，则获取现在最新的值，如果别人没修改过，直接修改共享数据的值**，CAS 这种机制也称之为乐观锁，综合性能较好

------

### Atomic 原子整数

#### 常用API

常见原子类：AtomicInteger、AtomicBoolean、AtomicLong

构造方法：

- `public AtomicInteger()`：初始化一个默认值为 0 的原子型 Integer
- `public AtomicInteger(int initialValue)`：初始化一个指定值的原子型 Integer

常用API：

| 方法                                  | 作用                                                         |
| ------------------------------------- | ------------------------------------------------------------ |
| public final int get()                | 获取 AtomicInteger 的值                                      |
| public final int getAndIncrement()    | 以原子方式将当前值加 1，返回的是自增前的值                   |
| public final int incrementAndGet()    | 以原子方式将当前值加 1，返回的是自增后的值                   |
| public final int getAndSet(int value) | 以原子方式设置为 newValue 的值，返回旧值                     |
| public final int addAndGet(int data)  | 以原子方式将输入的数值与实例中的值相加并返回 实例：AtomicInteger 里的 value |

------

#### 原理分析

**AtomicInteger 原理**：自旋锁 + CAS 算法

CAS 算法：有 3 个操作数（内存值 V， 旧的预期值 A，要修改的值 B）

- 当旧的预期值 A == 内存值 V 此时可以修改，将 V 改为 B
- 当旧的预期值 A != 内存值 V 此时不能修改，并重新获取现在的最新值，重新获取的动作就是自旋

分析 getAndSet 方法：

- AtomicInteger：

  ```java
  public final int getAndSet(int newValue) {
      /**
      * this: 		当前对象
      * valueOffset:	内存偏移量，内存地址
      */
      return unsafe.getAndSetInt(this, valueOffset, newValue);
  }
  ```

  valueOffset：偏移量表示该变量值相对于当前对象地址的偏移，Unsafe 就是根据内存偏移地址获取数据

  ```java
  valueOffset = unsafe.objectFieldOffset
                  (AtomicInteger.class.getDeclaredField("value"));
  //调用本地方法   -->
  public native long objectFieldOffset(Field var1);
  ```

- unsafe 类：

  ```java
  // val1: AtomicInteger对象本身，var2: 该对象值得引用地址，var4: 需要变动的数
  public final int getAndSetInt(Object var1, long var2, int var4) {
      int var5;
      do {
          // var5: 用 var1 和 var2 找到的内存中的真实值
          var5 = this.getIntVolatile(var1, var2);
      } while(!this.compareAndSwapInt(var1, var2, var5, var4));
  
      return var5;
  }
  ```

  var5：从主内存中拷贝到工作内存中的值（每次都要从主内存拿到最新的值到本地内存），然后执行 `compareAndSwapInt()` 再和主内存的值进行比较，假设方法返回 false，那么就一直执行 while 方法，直到期望的值和真实值一样，修改数据

- 变量 value 用 volatile 修饰，保证了多线程之间的内存可见性，避免线程从工作缓存中获取失效的变量

  ```
  private volatile int value
  ```

  **CAS 必须借助 volatile 才能读取到共享变量的最新值来实现比较并交换的效果**

分析 getAndUpdate 方法：

- getAndUpdate：

  ```java
  public final int getAndUpdate(IntUnaryOperator updateFunction) {
      int prev, next;
      do {
          prev = get();	//当前值，cas的期望值
          next = updateFunction.applyAsInt(prev);//期望值更新到该值
      } while (!compareAndSet(prev, next));//自旋
      return prev;
  }
  ```

  函数式接口：可以自定义操作逻辑

  ```java
  AtomicInteger a = new AtomicInteger();
  a.getAndUpdate(i -> i + 10);
  ```

- compareAndSet：

  ```java
  public final boolean compareAndSet(int expect, int update) {
      /**
      * this: 		当前对象
      * valueOffset:	内存偏移量，内存地址
      * expect:		期望的值
      * update: 		更新的值
      */
      return unsafe.compareAndSwapInt(this, valueOffset, expect, update);
  }
  ```

------

#### 原子引用

原子引用：对 Object 进行原子操作，提供一种读和写都是原子性的对象引用变量

原子引用类：AtomicReference、AtomicStampedReference、AtomicMarkableReference

AtomicReference 类：

- 构造方法：`AtomicReference<T> atomicReference = new AtomicReference<T>()`
- 常用 API：
  - `public final boolean compareAndSet(V expectedValue, V newValue)`：CAS 操作
  - `public final void set(V newValue)`：将值设置为 newValue
  - `public final V get()`：返回当前值

```java
public class AtomicReferenceDemo {
    public static void main(String[] args) {
        Student s1 = new Student(33, "z3");
        
        // 创建原子引用包装类
        AtomicReference<Student> atomicReference = new AtomicReference<>();
        // 设置主内存共享变量为s1
        atomicReference.set(s1);

        // 比较并交换，如果现在主物理内存的值为 z3，那么交换成 l4
        while (true) {
            Student s2 = new Student(44, "l4");
            if (atomicReference.compareAndSet(s1, s2)) {
                break;
            }
        }
        System.out.println(atomicReference.get());
    }
}

class Student {
    private int id;
    private String name;
    // ...
}
```

------

#### 原子数组

原子数组类：AtomicIntegerArray、AtomicLongArray、AtomicReferenceArray

AtomicIntegerArray 类方法：

```java
/**
*   i		the index
* expect 	the expected value
* update 	the new value
*/
public final boolean compareAndSet(int i, int expect, int update) {
    return compareAndSetRaw(checkedByteOffset(i), expect, update);
}
```

------

#### 原子更新器

原子更新器类：AtomicReferenceFieldUpdater、AtomicIntegerFieldUpdater、AtomicLongFieldUpdater

利用字段更新器，可以针对对象的某个域（Field）进行原子操作，只能配合 volatile 修饰的字段使用，否则会出现异常 `IllegalArgumentException: Must be volatile type`

常用 API：

- `static <U> AtomicIntegerFieldUpdater<U> newUpdater(Class<U> c, String fieldName)`：构造方法
- `abstract boolean compareAndSet(T obj, int expect, int update)`：CAS

```java
public class UpdateDemo {
    private volatile int field;
    
    public static void main(String[] args) {
        AtomicIntegerFieldUpdater fieldUpdater = AtomicIntegerFieldUpdater
            		.newUpdater(UpdateDemo.class, "field");
        UpdateDemo updateDemo = new UpdateDemo();
        fieldUpdater.compareAndSet(updateDemo, 0, 10);
        System.out.println(updateDemo.field);//10
    }
}
```

------

#### 原子累加器

原子累加器类：LongAdder、DoubleAdder、LongAccumulator、DoubleAccumulator

LongAdder 和 LongAccumulator 区别：

相同点：

- LongAddr 与 LongAccumulator 类都是使用非阻塞算法 CAS 实现的
- LongAddr 类是 LongAccumulator 类的一个特例，只是 LongAccumulator 提供了更强大的功能，可以自定义累加规则，当accumulatorFunction 为 null 时就等价于 LongAddr

不同点：

- 调用 casBase 时，LongAccumulator 使用 function.applyAsLong(b = base, x) 来计算，LongAddr 使用 casBase(b = base, b + x)
- LongAccumulator 类功能更加强大，构造方法参数中
  - accumulatorFunction 是一个双目运算器接口，可以指定累加规则，比如累加或者相乘，其根据输入的两个参数返回一个计算值，LongAdder 内置累加规则
  - identity 则是 LongAccumulator 累加器的初始值，LongAccumulator 可以为累加器提供非0的初始值，而 LongAdder 只能提供默认的 0

------

### Adder

#### 优化机制

LongAdder 是 Java8 提供的类，跟 AtomicLong 有相同的效果，但对 CAS 机制进行了优化，尝试使用分段 CAS 以及自动分段迁移的方式来大幅度提升多线程高并发执行 CAS 操作的性能

CAS 底层实现是在一个循环中不断地尝试修改目标值，直到修改成功。如果竞争不激烈修改成功率很高，否则失败率很高，失败后这些重复的原子性操作会耗费性能（导致大量线程**空循环，自旋转**）

优化核心思想：数据分离，将 AtomicLong 的**单点的更新压力分担到各个节点，空间换时间**，在低并发的时候直接更新，可以保障和 AtomicLong 的性能基本一致，而在高并发的时候通过分散减少竞争，提高了性能

**分段 CAS 机制**：

- 在发生竞争时，创建 Cell 数组用于将不同线程的操作离散（通过 hash 等算法映射）到不同的节点上
- 设置多个累加单元（会根据需要扩容，最大为 CPU 核数），Therad-0 累加 Cell[0]，而 Thread-1 累加 Cell[1] 等，最后将结果汇总
- 在累加时操作的不同的 Cell 变量，因此减少了 CAS 重试失败，从而提高性能

**自动分段迁移机制**：某个 Cell 的 value 执行 CAS 失败，就会自动寻找另一个 Cell 分段内的 value 值进行 CAS 操作

------

#### 伪共享

Cell 为累加单元：数组访问索引是通过 Thread 里的 threadLocalRandomProbe 域取模实现的，这个域是 ThreadLocalRandom 更新的

```java
// Striped64.Cell
@sun.misc.Contended static final class Cell {
    volatile long value;
    Cell(long x) { value = x; }
    // 用 cas 方式进行累加, prev 表示旧值, next 表示新值
    final boolean cas(long prev, long next) {
    	return UNSAFE.compareAndSwapLong(this, valueOffset, prev, next);
    }
    // 省略不重要代码
}
```

Cell 是数组形式，**在内存中是连续存储的**，64 位系统中，一个 Cell 为 24 字节（16 字节的对象头和 8 字节的 value），每一个 cache line 为 64 字节，因此缓存行可以存下 2 个的 Cell 对象，当 Core-0 要修改 Cell[0]、Core-1 要修改 Cell[1]，无论谁修改成功都会导致当前缓存行失效，从而导致对方的数据失效，需要重新去主存获取，影响效率

![image-20221220150457539](JUC.assets/image-20221220150457539.png)

@sun.misc.Contended：防止缓存行伪共享，在使用此注解的对象或字段的前后各增加 128 字节大小的 padding，使用 2 倍于大多数硬件缓存行让 CPU 将对象预读至缓存时**占用不同的缓存行**，这样就不会造成对方缓存行的失效

![image-20221220150512244](JUC.assets/image-20221220150512244.png)

------

#### 源码解析

Striped64 类成员属性：

```java
// 表示当前计算机CPU数量
static final int NCPU = Runtime.getRuntime().availableProcessors()
// 累加单元数组, 懒惰初始化
transient volatile Cell[] cells;
// 基础值, 如果没有竞争, 则用 cas 累加这个域，当 cells 扩容时，也会将数据写到 base 中
transient volatile long base;
// 在 cells 初始化或扩容时只能有一个线程执行, 通过 CAS 更新 cellsBusy 置为 1 来实现一个锁
transient volatile int cellsBusy;
```

工作流程：

- cells 占用内存是相对比较大的，是惰性加载的，在无竞争或者其他线程正在初始化 cells 数组的情况下，直接更新 base 域
- 在第一次发生竞争时（casBase 失败）会创建一个大小为 2 的 cells 数组，将当前累加的值包装为 Cell 对象，放入映射的槽位上
- 分段累加的过程中，如果当前线程对应的 cells 槽位为空，就会新建 Cell 填充，如果出现竞争，就会重新计算线程对应的槽位，继续自旋尝试修改
- 分段迁移后还出现竞争就会扩容 cells 数组长度为原来的两倍，然后 rehash，**数组长度总是 2 的 n 次幂**，默认最大为 CPU 核数，但是可以超过，如果核数是 6 核，数组最长是 8

方法分析：

- LongAdder#add：累加方法

  ```java
  public void add(long x) {
      // as 为累加单元数组的引用，b 为基础值，v 表示期望值
      // m 表示 cells 数组的长度 - 1，a 表示当前线程命中的 cell 单元格
      Cell[] as; long b, v; int m; Cell a;
      
      // cells 不为空说明 cells 已经被初始化，线程发生了竞争，去更新对应的 cell 槽位
      // 进入 || 后的逻辑去更新 base 域，更新失败表示发生竞争进入条件
      if ((as = cells) != null || !casBase(b = base, b + x)) {
          // uncontended 为 true 表示 cell 没有竞争
          boolean uncontended = true;
          
          // 条件一: true 说明 cells 未初始化，多线程写 base 发生竞争需要进行初始化 cells 数组
          //		  fasle 说明 cells 已经初始化，进行下一个条件寻找自己的 cell 去累加
          // 条件二: getProbe() 获取 hash 值，& m 的逻辑和 HashMap 的逻辑相同，保证散列的均匀性
          // 		  true 说明当前线程对应下标的 cell 为空，需要创建 cell
          //        false 说明当前线程对应的 cell 不为空，进行下一个条件【将 x 值累加到对应的 cell 中】
          // 条件三: 有取反符号，false 说明 cas 成功，直接返回，true 说明失败，当前线程对应的 cell 有竞争
          if (as == null || (m = as.length - 1) < 0 ||
              (a = as[getProbe() & m]) == null ||
              !(uncontended = a.cas(v = a.value, v + x)))
              longAccumulate(x, null, uncontended);
          	// 【uncontended 在对应的 cell 上累加失败的时候才为 false，其余情况均为 true】
      }
  }
  ```

- Striped64#longAccumulate：cell 数组创建

  ```java
  							// x  			null 			false | true
  final void longAccumulate(long x, LongBinaryOperator fn, boolean wasUncontended) {
      int h;
      // 当前线程还没有对应的 cell, 需要随机生成一个 hash 值用来将当前线程绑定到 cell
      if ((h = getProbe()) == 0) {
          // 初始化 probe，获取 hash 值
          ThreadLocalRandom.current(); 
          h = getProbe();	
          // 默认情况下 当前线程肯定是写入到了 cells[0] 位置，不把它当做一次真正的竞争
          wasUncontended = true;
      }
      // 表示【扩容意向】，false 一定不会扩容，true 可能会扩容
      boolean collide = false; 
      //自旋
      for (;;) {
          // as 表示cells引用，a 表示当前线程命中的 cell，n 表示 cells 数组长度，v 表示 期望值
          Cell[] as; Cell a; int n; long v;
          // 【CASE1】: 表示 cells 已经初始化了，当前线程应该将数据写入到对应的 cell 中
          if ((as = cells) != null && (n = as.length) > 0) {
              // CASE1.1: true 表示当前线程对应的索引下标的 Cell 为 null，需要创建 new Cell
              if ((a = as[(n - 1) & h]) == null) {
                  // 判断 cellsBusy 是否被锁
                  if (cellsBusy == 0) {   
                      // 创建 cell, 初始累加值为 x
                      Cell r = new Cell(x);  
                      // 加锁
                      if (cellsBusy == 0 && casCellsBusy()) {
                          // 创建成功标记，进入【创建 cell 逻辑】
                          boolean created = false;	
                          try {
                              Cell[] rs; int m, j;
                              // 把当前 cells 数组赋值给 rs，并且不为 null
                              if ((rs = cells) != null &&
                                  (m = rs.length) > 0 &&
                                  // 再次判断防止其它线程初始化过该位置，当前线程再次初始化该位置会造成数据丢失
                                  // 因为这里是线程安全的判断，进行的逻辑不会被其他线程影响
                                  rs[j = (m - 1) & h] == null) {
                                  // 把新创建的 cell 填充至当前位置
                                  rs[j] = r;
                                  created = true;	// 表示创建完成
                              }
                          } finally {
                              cellsBusy = 0;		// 解锁
                          }
                          if (created)			// true 表示创建完成，可以推出循环了
                              break;
                          continue;
                      }
                  }
                  collide = false;
              }
              // CASE1.2: 条件成立说明线程对应的 cell 有竞争, 改变线程对应的 cell 来重试 cas
              else if (!wasUncontended)
                  wasUncontended = true;
              // CASE 1.3: 当前线程 rehash 过，如果新命中的 cell 不为空，就尝试累加，false 说明新命中也有竞争
              else if (a.cas(v = a.value, ((fn == null) ? v + x : fn.applyAsLong(v, x))))
                  break;
              // CASE 1.4: cells 长度已经超过了最大长度 CPU 内核的数量或者已经扩容
              else if (n >= NCPU || cells != as)
                  collide = false; 		// 扩容意向改为false，【表示不能扩容了】
              // CASE 1.5: 更改扩容意向，如果 n >= NCPU，这里就永远不会执行到，case1.4 永远先于 1.5 执行
              else if (!collide)
                  collide = true;
              // CASE 1.6: 【扩容逻辑】，进行加锁
              else if (cellsBusy == 0 && casCellsBusy()) {
                  try {
                      // 线程安全的检查，防止期间被其他线程扩容了
                      if (cells == as) {     
                          // 扩容为以前的 2 倍
                          Cell[] rs = new Cell[n << 1];
                          // 遍历移动值
                          for (int i = 0; i < n; ++i)
                              rs[i] = as[i];
                          // 把扩容后的引用给 cells
                          cells = rs;
                      }
                  } finally {
                      cellsBusy = 0;	// 解锁
                  }
                  collide = false;	// 扩容意向改为 false，表示不扩容了
                  continue;
              }
              // 重置当前线程 Hash 值，这就是【分段迁移机制】
              h = advanceProbe(h);
          }
  
          // 【CASE2】: 运行到这说明 cells 还未初始化，as 为null
          // 判断是否没有加锁，没有加锁就用 CAS 加锁
          // 条件二判断是否其它线程在当前线程给 as 赋值之后修改了 cells，这里不是线程安全的判断
          else if (cellsBusy == 0 && cells == as && casCellsBusy()) {
              // 初始化标志，开始 【初始化 cells 数组】
              boolean init = false;
              try { 
                 	// 再次判断 cells == as 防止其它线程已经提前初始化了，当前线程再次初始化导致丢失数据
                  // 因为这里是【线程安全的，重新检查，经典 DCL】
                  if (cells == as) {
                      Cell[] rs = new Cell[2];	// 初始化数组大小为2
                      rs[h & 1] = new Cell(x);	// 填充线程对应的cell
                      cells = rs;
                      init = true;				// 初始化成功，标记置为 true
                  }
              } finally {
                  cellsBusy = 0;					// 解锁啊
              }
              if (init)
                  break;							// 初始化成功直接跳出自旋
          }
          // 【CASE3】: 运行到这说明其他线程在初始化 cells，当前线程将值累加到 base，累加成功直接结束自旋
          else if (casBase(v = base, ((fn == null) ? v + x :
                                      fn.applyAsLong(v, x))))
              break; 
      }
  }
  ```

- sum：获取最终结果通过 sum 整合，**保证最终一致性，不保证强一致性**

  ```java
  public long sum() {
      Cell[] as = cells; Cell a;
      long sum = base;
      if (as != null) {
          // 遍历 累加
          for (int i = 0; i < as.length; ++i) {
              if ((a = as[i]) != null)
                  sum += a.value;
          }
      }
      return sum;
  }
  ```

------

### ABA

ABA 问题：当进行获取主内存值时，该内存值在写入主内存时已经被修改了 N 次，但是最终又改成原来的值

其他线程先把 A 改成 B 又改回 A，主线程**仅能判断出共享变量的值与最初值 A 是否相同**，不能感知到这种从 A 改为 B 又 改回 A 的情况，这时 CAS 虽然成功，但是过程存在问题

- 构造方法：
  - `public AtomicStampedReference(V initialRef, int initialStamp)`：初始值和初始版本号
- 常用API：
  - ` public boolean compareAndSet(V expectedReference, V newReference, int expectedStamp, int newStamp)`：**期望引用和期望版本号都一致**才进行 CAS 修改数据
  - `public void set(V newReference, int newStamp)`：设置值和版本号
  - `public V getReference()`：返回引用的值
  - `public int getStamp()`：返回当前版本号

```java
public static void main(String[] args) {
    AtomicStampedReference<Integer> atomicReference = new AtomicStampedReference<>(100,1);
    int startStamp = atomicReference.getStamp();
    new Thread(() ->{
        int stamp = atomicReference.getStamp();
        atomicReference.compareAndSet(100, 101, stamp, stamp + 1);
        stamp = atomicReference.getStamp();
        atomicReference.compareAndSet(101, 100, stamp, stamp + 1);
    },"t1").start();

    new Thread(() ->{
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!atomicReference.compareAndSet(100, 200, startStamp, startStamp + 1)) {
            System.out.println(atomicReference.getReference());//100
            System.out.println(Thread.currentThread().getName() + "线程修改失败");
        }
    },"t2").start();
}
```

------

### Unsafe

Unsafe 是 CAS 的核心类，由于 Java 无法直接访问底层系统，需要通过本地（Native）方法来访问

Unsafe 类存在 sun.misc 包，其中所有方法都是 native 修饰的，都是直接调用**操作系统底层资源**执行相应的任务，基于该类可以直接操作特定的内存数据，其内部方法操作类似 C 的指针

模拟实现原子整数：

```java
public static void main(String[] args) {
    MyAtomicInteger atomicInteger = new MyAtomicInteger(10);
    if (atomicInteger.compareAndSwap(20)) {
        System.out.println(atomicInteger.getValue());
    }
}

class MyAtomicInteger {
    private static final Unsafe UNSAFE;
    private static final long VALUE_OFFSET;
    private volatile int value;

    static {
        try {
            //Unsafe unsafe = Unsafe.getUnsafe()这样会报错，需要反射获取
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafe.get(null);
            // 获取 value 属性的内存地址，value 属性指向该地址，直接设置该地址的值可以修改 value 的值
            VALUE_OFFSET = UNSAFE.objectFieldOffset(
                		   MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public MyAtomicInteger(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    public boolean compareAndSwap(int update) {
        while (true) {
            int prev = this.value;
            int next = update;
            //							当前对象  内存偏移量    期望值 更新值
            if (UNSAFE.compareAndSwapInt(this, VALUE_OFFSET, prev, update)) {
                System.out.println("CAS成功");
                return true;
            }
        }
    }
}
```

------

### final

#### 原理

```java
public class TestFinal {
	final int a = 20;
}
```

字节码：

```java
0: aload_0
1: invokespecial #1 // Method java/lang/Object."<init>":()V
4: aload_0
5: bipush 20		// 将值直接放入栈中
7: putfield #2 		// Field a:I
<-- 写屏障
10: return
```

final 变量的赋值通过 putfield 指令来完成，在这条指令之后也会加入写屏障，保证在其它线程读到它的值时不会出现为 0 的情况

其他线程访问 final 修饰的变量**会复制一份放入栈中**，效率更高

------

#### 不可变

不可变：如果一个对象不能够修改其内部状态（属性），那么就是不可变对象

不可变对象线程安全的，不存在并发修改和可见性问题，是另一种避免竞争的方式

String 类也是不可变的，该类和类中所有属性都是 final 的

- 类用 final 修饰保证了该类中的方法不能被覆盖，防止子类无意间破坏不可变性

- 无写入方法（set）确保外部不能对内部属性进行修改

- 属性用 final 修饰保证了该属性是只读的，不能修改

  ```java
  public final class String
      implements java.io.Serializable, Comparable<String>, CharSequence {
      /** The value is used for character storage. */
      private final char value[];
      //....
  }
  ```

- 更改 String 类数据时，会构造新字符串对象，生成新的 char[] value，通过**创建副本对象来避免共享的方式称之为保护性拷贝**

------

### State

无状态：成员变量保存的数据也可以称为状态信息，无状态就是没有成员变量

Servlet 为了保证其线程安全，一般不为 Servlet 设置成员变量，这种没有任何成员变量的类是线程安全的

------

## 享元模式

### 包装类

在JDK中，Boolean，Byte，Short，Integer, Long, Character 等包装类提供了 valueOf 方法，例如 Long的valueOf 会缓存 -128 ~ 127 之间的Long对象，在这个范围之间用重用对象，大于这个范围，才会新建Long对象：

```java
public static long valueOf(long l){
    final int offset = 128;
    if (l >= -128 && l <= 127){
        // will cache
        return LongCache.cache[(int)l + offset];
    }
    return new Long(1);
}
```

> 注意：
>
> Byte, Short, Long 缓存的范围都是 -128~127
>
> Character 缓存的范围是 0~127
>
> Integer 的默认范围是 -128~127，最小值不能变，但最大值可以通过调整虚拟机参数 - Djava.lang.Integer.IntegerCache.high 来改变
>
> Boolean 缓存了 TRUE 和 FALSE

### String 串池

### BigDecimal BigInteger

------

### Local

#### 基本介绍

ThreadLocal 类用来提供线程内部的局部变量，这种变量在多线程环境下访问（通过 get 和 set 方法访问）时能保证各个线程的变量相对独立于其他线程内的变量，分配在堆内的 **TLAB** 中

ThreadLocal 实例通常来说都是 `private static` 类型的，属于一个线程的本地变量，用于关联线程和线程上下文。每个线程都会在 ThreadLocal 中保存一份该线程独有的数据，所以是线程安全的

ThreadLocal 作用：

- 线程并发：应用在多线程并发的场景下
- 传递数据：通过 ThreadLocal 实现在同一线程不同函数或组件中传递公共变量，减少传递复杂度
- 线程隔离：每个线程的变量都是独立的，不会互相影响

对比 synchronized：

|        | synchronized                                                 | ThreadLocal                                                  |
| ------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 原理   | 同步机制采用**以时间换空间**的方式，只提供了一份变量，让不同的线程排队访问 | ThreadLocal 采用**以空间换时间**的方式，为每个线程都提供了一份变量的副本，从而实现同时访问而相不干扰 |
| 侧重点 | 多个线程之间访问资源的同步                                   | 多线程中让每个线程之间的数据相互隔离                         |

------

#### 基本使用

##### 常用方法

| 方法                       | 描述                         |
| -------------------------- | ---------------------------- |
| ThreadLocal<>()            | 创建 ThreadLocal 对象        |
| protected T initialValue() | 返回当前线程局部变量的初始值 |
| public void set( T value)  | 设置当前线程绑定的局部变量   |
| public T get()             | 获取当前线程绑定的局部变量   |
| public void remove()       | 移除当前线程绑定的局部变量   |

```java
public class MyDemo {

    private static ThreadLocal<String> tl = new ThreadLocal<>();

    private String content;

    private String getContent() {
        // 获取当前线程绑定的变量
        return tl.get();
    }

    private void setContent(String content) {
        // 变量content绑定到当前线程
        tl.set(content);
    }

    public static void main(String[] args) {
        MyDemo demo = new MyDemo();
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // 设置数据
                    demo.setContent(Thread.currentThread().getName() + "的数据");
                    System.out.println("-----------------------");
                    System.out.println(Thread.currentThread().getName() + "--->" + demo.getContent());
                }
            });
            thread.setName("线程" + i);
            thread.start();
        }
    }
}
```

------

##### 应用场景

ThreadLocal 适用于下面两种场景：

- 每个线程需要有自己单独的实例
- 实例需要在多个方法中共享，但不希望被多线程共享

ThreadLocal 方案有两个突出的优势：

1. 传递数据：保存每个线程绑定的数据，在需要的地方可以直接获取，避免参数直接传递带来的代码耦合问题
2. 线程隔离：各线程之间的数据相互隔离却又具备并发性，避免同步方式带来的性能损失

ThreadLocal 用于数据连接的事务管理：

```java
public class JdbcUtils {
    // ThreadLocal对象，将connection绑定在当前线程中
    private static final ThreadLocal<Connection> tl = new ThreadLocal();
    // c3p0 数据库连接池对象属性
    private static final ComboPooledDataSource ds = new ComboPooledDataSource();
    // 获取连接
    public static Connection getConnection() throws SQLException {
        //取出当前线程绑定的connection对象
        Connection conn = tl.get();
        if (conn == null) {
            //如果没有，则从连接池中取出
            conn = ds.getConnection();
            //再将connection对象绑定到当前线程中，非常重要的操作
            tl.set(conn);
        }
        return conn;
    }
	// ...
}
```

用 ThreadLocal 使 SimpleDateFormat 从独享变量变成单个线程变量：

```java
public class ThreadLocalDateUtil {
    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static Date parse(String dateStr) throws ParseException {
        return threadLocal.get().parse(dateStr);
    }

    public static String format(Date date) {
        return threadLocal.get().format(date);
    }
}
```

------

#### 实现原理

##### 底层结构

JDK8 以前：每个 ThreadLocal 都创建一个 Map，然后用线程作为 Map 的 key，要存储的局部变量作为 Map 的 value，达到各个线程的局部变量隔离的效果。这种结构会造成 Map 结构过大和内存泄露，因为 Thread 停止后无法通过 key 删除对应的数据

<img src="JUC.assets/image-20221220203052649.png" alt="image-20221220203052649" style="zoom:50%;" />

JDK8 以后：每个 Thread 维护一个 ThreadLocalMap，这个 Map 的 key 是 ThreadLocal 实例本身，value 是真正要存储的值

- **每个 Thread 线程内部都有一个 Map (ThreadLocalMap)**
- Map 里面存储 ThreadLocal 对象（key）和线程的私有变量（value）
- Thread 内部的 Map 是由 ThreadLocal 维护的，由 ThreadLocal 负责向 map 获取和设置线程的变量值
- 对于不同的线程，每次获取副本值时，别的线程并不能获取到当前线程的副本值，形成副本的隔离，互不干扰

<img src="JUC.assets/image-20221220203225525.png" alt="image-20221220203225525" style="zoom:50%;" />

JDK8 前后对比：

- 每个 Map 存储的 Entry 数量会变少，因为之前的存储数量由 Thread 的数量决定，现在由 ThreadLocal 的数量决定，在实际编程当中，往往 ThreadLocal 的数量要少于 Thread 的数量
- 当 Thread 销毁之后，对应的 ThreadLocalMap 也会随之销毁，能减少内存的使用，**防止内存泄露**

------

##### 成员变量

- Thread 类的相关属性：**每一个线程持有一个 ThreadLocalMap 对象**，存放由 ThreadLocal 和数据组成的 Entry 键值对

  ```java
  ThreadLocal.ThreadLocalMap threadLocals = null
  ```

- 计算 ThreadLocal 对象的哈希值：

  ```java
  private final int threadLocalHashCode = nextHashCode()
  ```

  使用 `threadLocalHashCode & (table.length - 1)` 计算当前 entry 需要存放的位置

- 每创建一个 ThreadLocal 对象就会使用 nextHashCode 分配一个 hash 值给这个对象：

  ```java
  private static AtomicInteger nextHashCode = new AtomicInteger()
  ```

- 斐波那契数也叫黄金分割数，hash 的**增量**就是这个数字，带来的好处是 hash 分布非常均匀：

  ```java
  private static final int HASH_INCREMENT = 0x61c88647
  ```

------

##### 成员方法

方法都是线程安全的，因为 ThreadLocal 属于一个线程的，ThreadLocal 中的方法，逻辑都是获取当前线程维护的 ThreadLocalMap 对象，然后进行数据的增删改查，没有指定初始值的 threadlcoal 对象默认赋值为 null

- initialValue()：返回该线程局部变量的初始值

  - 延迟调用的方法，在执行 get 方法时才执行
  - 该方法缺省（默认）实现直接返回一个 null
  - 如果想要一个初始值，可以重写此方法， 该方法是一个 `protected` 的方法，为了让子类覆盖而设计的

  ```java
  protected T initialValue() {
      return null;
  }
  ```

- nextHashCode()：计算哈希值，ThreadLocal 的散列方式称之为**斐波那契散列**，每次获取哈希值都会加上 HASH_INCREMENT，这样做可以尽量避免 hash 冲突，让哈希值能均匀的分布在 2 的 n 次方的数组中

  ```java
  private static int nextHashCode() {
      // 哈希值自增一个 HASH_INCREMENT 数值
      return nextHashCode.getAndAdd(HASH_INCREMENT);
  }
  ```

- set()：修改当前线程与当前 threadlocal 对象相关联的线程局部变量

  ```java
  public void set(T value) {
      // 获取当前线程对象
      Thread t = Thread.currentThread();
      // 获取此线程对象中维护的 ThreadLocalMap 对象
      ThreadLocalMap map = getMap(t);
      // 判断 map 是否存在
      if (map != null)
          // 调用 threadLocalMap.set 方法进行重写或者添加
          map.set(this, value);
      else
          // map 为空，调用 createMap 进行 ThreadLocalMap 对象的初始化。参数1是当前线程，参数2是局部变量
          createMap(t, value);
  }
  ```

  ```java
  // 获取当前线程 Thread 对应维护的 ThreadLocalMap 
  ThreadLocalMap getMap(Thread t) {
      return t.threadLocals;
  }
  // 创建当前线程Thread对应维护的ThreadLocalMap 
  void createMap(Thread t, T firstValue) {
      // 【这里的 this 是调用此方法的 threadLocal】，创建一个新的 Map 并设置第一个数据
      t.threadLocals = new ThreadLocalMap(this, firstValue);
  }
  ```

- get()：获取当前线程与当前 ThreadLocal 对象相关联的线程局部变量

  ```java
  public T get() {
      Thread t = Thread.currentThread();
      ThreadLocalMap map = getMap(t);
      // 如果此map存在
      if (map != null) {
          // 以当前的 ThreadLocal 为 key，调用 getEntry 获取对应的存储实体 e
          ThreadLocalMap.Entry e = map.getEntry(this);
          // 对 e 进行判空 
          if (e != null) {
              // 获取存储实体 e 对应的 value值
              T result = (T)e.value;
              return result;
          }
      }
      /*有两种情况有执行当前代码
        第一种情况: map 不存在，表示此线程没有维护的 ThreadLocalMap 对象
        第二种情况: map 存在, 但是【没有与当前 ThreadLocal 关联的 entry】，就会设置为默认值 */
      // 初始化当前线程与当前 threadLocal 对象相关联的 value
      return setInitialValue();
  }
  ```

  ```java
  private T setInitialValue() {
      // 调用initialValue获取初始化的值，此方法可以被子类重写, 如果不重写默认返回 null
      T value = initialValue();
      Thread t = Thread.currentThread();
      ThreadLocalMap map = getMap(t);
      // 判断 map 是否初始化过
      if (map != null)
          // 存在则调用 map.set 设置此实体 entry，value 是默认的值
          map.set(this, value);
      else
          // 调用 createMap 进行 ThreadLocalMap 对象的初始化中
          createMap(t, value);
      // 返回线程与当前 threadLocal 关联的局部变量
      return value;
  }
  ```

- remove()：移除当前线程与当前 threadLocal 对象相关联的线程局部变量

  ```java
  public void remove() {
      // 获取当前线程对象中维护的 ThreadLocalMap 对象
      ThreadLocalMap m = getMap(Thread.currentThread());
      if (m != null)
          // map 存在则调用 map.remove，this时当前ThreadLocal，以this为key删除对应的实体
          m.remove(this);
  }
  ```

------

#### LocalMap

##### 成员属性

ThreadLocalMap 是 ThreadLocal 的内部类，没有实现 Map 接口，用独立的方式实现了 Map 的功能，其内部 Entry 也是独立实现

```java
// 初始化当前 map 内部散列表数组的初始长度 16
private static final int INITIAL_CAPACITY = 16;

// 存放数据的table，数组长度必须是2的整次幂。
private Entry[] table;

// 数组里面 entrys 的个数，可以用于判断 table 当前使用量是否超过阈值
private int size = 0;

// 进行扩容的阈值，表使用量大于它的时候进行扩容。
private int threshold;
```

存储结构 Entry：

- Entry 继承 WeakReference，key 是弱引用，目的是将 ThreadLocal 对象的生命周期和线程生命周期解绑
- Entry 限制只能用 ThreadLocal 作为 key，key 为 null (entry.get() == null) 意味着 key 不再被引用，entry 也可以从 table 中清除

```java
static class Entry extends WeakReference<ThreadLocal<?>> {
    Object value;
    Entry(ThreadLocal<?> k, Object v) {
        // this.referent = referent = key;
        super(k);
        value = v;
    }
}
```

构造方法：延迟初始化的，线程第一次存储 threadLocal - value 时才会创建 threadLocalMap 对象

```java
ThreadLocalMap(ThreadLocal<?> firstKey, Object firstValue) {
    // 初始化table，创建一个长度为16的Entry数组
    table = new Entry[INITIAL_CAPACITY];
    // 【寻址算法】计算索引
    int i = firstKey.threadLocalHashCode & (INITIAL_CAPACITY - 1);
    // 创建 entry 对象，存放到指定位置的 slot 中
    table[i] = new Entry(firstKey, firstValue);
    // 数据总量是 1
    size = 1;
    // 将阈值设置为 （当前数组长度 * 2）/ 3。
    setThreshold(INITIAL_CAPACITY);
}
```

------

##### 成员方法

- set()：添加数据，ThreadLocalMap 使用**线性探测法来解决哈希冲突**

  - 该方法会一直探测下一个地址，直到有空的地址后插入，若插入后 Map 数量超过阈值，数组会扩容为原来的 2 倍

    假设当前 table 长度为16，计算出来 key 的 hash 值为 14，如果 table[14] 上已经有值，并且其 key 与当前 key 不一致，那么就发生了 hash 冲突，这个时候将 14 加 1 得到 15，取 table[15] 进行判断，如果还是冲突会回到 0，取 table[0]，以此类推，直到可以插入，可以把 Entry[] table 看成一个**环形数组**

  - 线性探测法会出现**堆积问题**，可以采取平方探测法解决

  - 在探测过程中 ThreadLocal 会复用 key 为 null 的脏 Entry 对象，并进行垃圾清理，防止出现内存泄漏

  ```java
  private void set(ThreadLocal<?> key, Object value) {
      // 获取散列表
      ThreadLocal.ThreadLocalMap.Entry[] tab = table;
      int len = tab.length;
      // 哈希寻址
      int i = key.threadLocalHashCode & (len-1);
      // 使用线性探测法向后查找元素，碰到 entry 为空时停止探测
      for (ThreadLocal.ThreadLocalMap.Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
          // 获取当前元素 key
          ThreadLocal<?> k = e.get();
          // ThreadLocal 对应的 key 存在，【直接覆盖之前的值】
          if (k == key) {
              e.value = value;
              return;
          }
          // 【这两个条件谁先成立不一定，所以 replaceStaleEntry 中还需要判断 k == key 的情况】
          
          // key 为 null，但是值不为 null，说明之前的 ThreadLocal 对象已经被回收了，当前是【过期数据】
          if (k == null) {
              // 【碰到一个过期的 slot，当前数据复用该槽位，替换过期数据】
              // 这个方法还进行了垃圾清理动作，防止内存泄漏
              replaceStaleEntry(key, value, i);
              return;
          }
      }
  	// 逻辑到这说明碰到 slot == null 的位置，则在空元素的位置创建一个新的 Entry
      tab[i] = new Entry(key, value);
      // 数量 + 1
      int sz = ++size;
      
      // 【做一次启发式清理】，如果没有清除任何 entry 并且【当前使用量达到了负载因子所定义，那么进行 rehash
      if (!cleanSomeSlots(i, sz) && sz >= threshold)
          // 扩容
          rehash();
  }
  ```

  ```java
  // 获取【环形数组】的下一个索引
  private static int nextIndex(int i, int len) {
      // 索引越界后从 0 开始继续获取
      return ((i + 1 < len) ? i + 1 : 0);
  }
  ```

  ```java
  // 在指定位置插入指定的数据
  private void replaceStaleEntry(ThreadLocal<?> key, Object value, int staleSlot) {
      // 获取散列表
      Entry[] tab = table;
      int len = tab.length;
      Entry e;
  	// 探测式清理的开始下标，默认从当前 staleSlot 开始
      int slotToExpunge = staleSlot;
      // 以当前 staleSlot 开始【向前迭代查找】，找到索引靠前过期数据，找到以后替换 slotToExpunge 值
      // 【保证在一个区间段内，从最前面的过期数据开始清理】
      for (int i = prevIndex(staleSlot, len); (e = tab[i]) != null; i = prevIndex(i, len))
          if (e.get() == null)
              slotToExpunge = i;
  
  	// 以 staleSlot 【向后去查找】，直到碰到 null 为止，还是线性探测
      for (int i = nextIndex(staleSlot, len); (e = tab[i]) != null; i = nextIndex(i, len)) {
          // 获取当前节点的 key
          ThreadLocal<?> k = e.get();
  		// 条件成立说明是【替换逻辑】
          if (k == key) {
              e.value = value;
              // 因为本来要在 staleSlot 索引处插入该数据，现在找到了i索引处的key与数据一致
              // 但是 i 位置距离正确的位置更远，因为是向后查找，所以还是要在 staleSlot 位置插入当前 entry
              // 然后将 table[staleSlot] 这个过期数据放到当前循环到的 table[i] 这个位置，
              tab[i] = tab[staleSlot];
              tab[staleSlot] = e;
  			
              // 条件成立说明向前查找过期数据并未找到过期的 entry，但 staleSlot 位置已经不是过期数据了，i 位置才是
              if (slotToExpunge == staleSlot)
                  slotToExpunge = i;
              
              // 【清理过期数据，expungeStaleEntry 探测式清理，cleanSomeSlots 启发式清理】
              cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
              return;
          }
  		// 条件成立说明当前遍历的 entry 是一个过期数据，并且该位置前面也没有过期数据
          if (k == null && slotToExpunge == staleSlot)
              // 探测式清理过期数据的开始下标修改为当前循环的 index，因为 staleSlot 会放入要添加的数据
              slotToExpunge = i;
      }
  	// 向后查找过程中并未发现 k == key 的 entry，说明当前是一个【取代过期数据逻辑】
      // 删除原有的数据引用，防止内存泄露
      tab[staleSlot].value = null;
      // staleSlot 位置添加数据，【上面的所有逻辑都不会更改 staleSlot 的值】
      tab[staleSlot] = new Entry(key, value);
  
      // 条件成立说明除了 staleSlot 以外，还发现其它的过期 slot，所以要【开启清理数据的逻辑】
      if (slotToExpunge != staleSlot)
          cleanSomeSlots(expungeStaleEntry(slotToExpunge), len);
  }
  ```

![image-20221220205501388](JUC.assets/image-20221220205501388.png)

- ```java
  private static int prevIndex(int i, int len) {
      // 形成一个环绕式的访问，头索引越界后置为尾索引
      return ((i - 1 >= 0) ? i - 1 : len - 1);
  }
  ```

- getEntry()：ThreadLocal 的 get 方法以当前的 ThreadLocal 为 key，调用 getEntry 获取对应的存储实体 e

  ```java
  private Entry getEntry(ThreadLocal<?> key) {
      // 哈希寻址
      int i = key.threadLocalHashCode & (table.length - 1);
      // 访问散列表中指定指定位置的 slot 
      Entry e = table[i];
      // 条件成立，说明 slot 有值并且 key 就是要寻找的 key，直接返回
      if (e != null && e.get() == key)
          return e;
      else
          // 进行线性探测
          return getEntryAfterMiss(key, i, e);
  }
  // 线性探测寻址
  private Entry getEntryAfterMiss(ThreadLocal<?> key, int i, Entry e) {
      // 获取散列表
      Entry[] tab = table;
      int len = tab.length;
  
      // 开始遍历，碰到 slot == null 的情况，搜索结束
      while (e != null) {
  		// 获取当前 slot 中 entry 对象的 key
          ThreadLocal<?> k = e.get();
          // 条件成立说明找到了，直接返回
          if (k == key)
              return e;
          if (k == null)
               // 过期数据，【探测式过期数据回收】
              expungeStaleEntry(i);
          else
              // 更新 index 继续向后走
              i = nextIndex(i, len);
          // 获取下一个槽位中的 entry
          e = tab[i];
      }
      // 说明当前区段没有找到相应数据
      // 【因为存放数据是线性的向后寻找槽位，都是紧挨着的，不可能越过一个 空槽位 在后面放】，可以减少遍历的次数
      return null;
  }
  ```

- rehash()：触发一次全量清理，如果数组长度大于等于长度的 `2/3 * 3/4 = 1/2`，则进行 resize

  ```java
  private void rehash() {
      // 清楚当前散列表内的【所有】过期的数据
      expungeStaleEntries();
      
      // threshold = len * 2 / 3，就是 2/3 * (1 - 1/4)
      if (size >= threshold - threshold / 4)
          resize();
  }
  ```

  ```java
  private void expungeStaleEntries() {
      Entry[] tab = table;
      int len = tab.length;
      // 【遍历所有的槽位，清理过期数据】
      for (int j = 0; j < len; j++) {
          Entry e = tab[j];
          if (e != null && e.get() == null)
              expungeStaleEntry(j);
      }
  }
  ```

  Entry **数组为扩容为原来的 2 倍** ，重新计算 key 的散列值，如果遇到 key 为 null 的情况，会将其 value 也置为 null，帮助 GC

  ```java
  private void resize() {
      Entry[] oldTab = table;
      int oldLen = oldTab.length;
      // 新数组的长度是老数组的二倍
      int newLen = oldLen * 2;
      Entry[] newTab = new Entry[newLen];
      // 统计新table中的entry数量
      int count = 0;
  	// 遍历老表，进行【数据迁移】
      for (int j = 0; j < oldLen; ++j) {
          // 访问老表的指定位置的 entry
          Entry e = oldTab[j];
          // 条件成立说明老表中该位置有数据，可能是过期数据也可能不是
          if (e != null) {
              ThreadLocal<?> k = e.get();
              // 过期数据
              if (k == null) {
                  e.value = null; // Help the GC
              } else {
                  // 非过期数据，在新表中进行哈希寻址
                  int h = k.threadLocalHashCode & (newLen - 1);
                  // 【线程探测】
                  while (newTab[h] != null)
                      h = nextIndex(h, newLen);
                  // 将数据存放到新表合适的 slot 中
                  newTab[h] = e;
                  count++;
              }
          }
      }
  	// 设置下一次触发扩容的指标：threshold = len * 2 / 3;
      setThreshold(newLen);
      size = count;
      // 将扩容后的新表赋值给 threadLocalMap 内部散列表数组引用
      table = newTab;
  }
  ```

- remove()：删除 Entry

  ```java
  private void remove(ThreadLocal<?> key) {
      Entry[] tab = table;
      int len = tab.length;
      // 哈希寻址
      int i = key.threadLocalHashCode & (len-1);
      for (Entry e = tab[i]; e != null; e = tab[i = nextIndex(i, len)]) {
          // 找到了对应的 key
          if (e.get() == key) {
              // 设置 key 为 null
              e.clear();
              // 探测式清理
              expungeStaleEntry(i);
              return;
          }
      }
  }
  ```

------

##### 清理方法

- 探测式清理：沿着开始位置向后探测清理过期数据，沿途中碰到未过期数据则将此数据 rehash 在 table 数组中的定位，重定位后的元素理论上更接近 `i = entry.key & (table.length - 1)`，让**数据的排列更紧凑**，会优化整个散列表查询性能

  ```java
  // table[staleSlot] 是一个过期数据，以这个位置开始继续向后查找过期数据
  private int expungeStaleEntry(int staleSlot) {
      // 获取散列表和数组长度
      Entry[] tab = table;
      int len = tab.length;
  
      // help gc，先把当前过期的 entry 置空，在取消对 entry 的引用
      tab[staleSlot].value = null;
      tab[staleSlot] = null;
      // 数量-1
      size--;
  
      Entry e;
      int i;
      // 从 staleSlot 开始向后遍历，直到碰到 slot == null 结束，【区间内清理过期数据】
      for (i = nextIndex(staleSlot, len); (e = tab[i]) != null; i = nextIndex(i, len)) {
          ThreadLocal<?> k = e.get();
          // 当前 entry 是过期数据
          if (k == null) {
              // help gc
              e.value = null;
              tab[i] = null;
              size--;
          } else {
              // 当前 entry 不是过期数据的逻辑，【rehash】
              // 重新计算当前 entry 对应的 index
              int h = k.threadLocalHashCode & (len - 1);
              // 条件成立说明当前 entry 存储时发生过 hash 冲突，向后偏移过了
              if (h != i) {
                  // 当前位置置空
                  tab[i] = null;
                  // 以正确位置 h 开始，向后查找第一个可以存放 entry 的位置
                  while (tab[h] != null)
                      h = nextIndex(h, len);
                  // 将当前元素放入到【距离正确位置更近的位置，有可能就是正确位置】
                  tab[h] = e;
              }
          }
      }
      // 返回 slot = null 的槽位索引，图例是 7，这个索引代表【索引前面的区间已经清理完成垃圾了】
      return i;
  }
  ```

<img src="JUC.assets/image-20221220205553576.png" alt="image-20221220205553576" style="zoom:33%;" />

<img src="JUC.assets/image-20221220205617101.png" alt="image-20221220205617101" style="zoom:33%;" />

- 启发式清理：向后循环扫描过期数据，发现过期数据调用探测式清理方法，如果连续几次的循环都没有发现过期数据，就停止扫描

  ```java
  //  i 表示启发式清理工作开始位置，一般是空 slot，n 一般传递的是 table.length 
  private boolean cleanSomeSlots(int i, int n) {
      // 表示启发式清理工作是否清除了过期数据
      boolean removed = false;
      // 获取当前 map 的散列表引用
      Entry[] tab = table;
      int len = tab.length;
      do {
          // 获取下一个索引，因为探测式返回的 slot 为 null
          i = nextIndex(i, len);
          Entry e = tab[i];
          // 条件成立说明是过期的数据，key 被 gc 了
          if (e != null && e.get() == null) {
              // 【发现过期数据重置 n 为数组的长度】
              n = len;
              // 表示清理过过期数据
              removed = true;
              // 以当前过期的 slot 为开始节点 做一次探测式清理工作
              i = expungeStaleEntry(i);
          }
          // 假设 table 长度为 16
          // 16 >>> 1 ==> 8，8 >>> 1 ==> 4，4 >>> 1 ==> 2，2 >>> 1 ==> 1，1 >>> 1 ==> 0
          // 连续经过这么多次循环【没有扫描到过期数据】，就停止循环，扫描到空 slot 不算，因为不是过期数据
      } while ((n >>>= 1) != 0);
      
      // 返回清除标记
      return removed;
  }
  ```

参考视频：https://space.bilibili.com/457326371/

------

#### 内存泄漏

Memory leak：内存泄漏是指程序中动态分配的堆内存由于某种原因未释放或无法释放，造成系统内存的浪费，导致程序运行速度减慢甚至系统崩溃等严重后果，内存泄漏的堆积终将导致内存溢出

- 如果 key 使用强引用：使用完 ThreadLocal ，threadLocal Ref 被回收，但是 threadLocalMap 的 Entry 强引用了 threadLocal，造成 threadLocal 无法被回收，无法完全避免内存泄漏

<img src="JUC.assets/image-20221220205706874.png" alt="image-20221220205706874" style="zoom:50%;" />

如果 key 使用弱引用：使用完 ThreadLocal ，threadLocal Ref 被回收，ThreadLocalMap 只持有 ThreadLocal 的弱引用，所以threadlocal 也可以被回收，此时 Entry 中的 key = null。但没有手动删除这个 Entry 或者 CurrentThread 依然运行，依然存在强引用链，value 不会被回收，而这块 value 永远不会被访问到，也会导致 value 内存泄漏

<img src="JUC.assets/image-20221220205727143.png" alt="image-20221220205727143" style="zoom:50%;" />

- 两个主要原因：
  - 没有手动删除这个 Entry
  - CurrentThread 依然运行

根本原因：ThreadLocalMap 是 Thread的一个属性，**生命周期跟 Thread 一样长**，如果没有手动删除对应 Entry 就会导致内存泄漏

解决方法：使用完 ThreadLocal 中存储的内容后将它 remove 掉就可以

ThreadLocal 内部解决方法：在 ThreadLocalMap 中的 set/getEntry 方法中，通过线性探测法对 key 进行判断，如果 key 为 null（ThreadLocal 为 null）会对 Entry 进行垃圾回收。所以**使用弱引用比强引用多一层保障**，就算不调用 remove，也有机会进行 GC

------

#### 变量传递

##### 基本使用

父子线程：**创建子线程的线程是父线程**，比如实例中的 main 线程就是父线程

ThreadLocal 中存储的是线程的局部变量，如果想实现线程间局部变量传递可以使用 InheritableThreadLocal 类

```java
public static void main(String[] args) {
    ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
    threadLocal.set("父线程设置的值");

    new Thread(() -> System.out.println("子线程输出：" + threadLocal.get())).start();
}
// 子线程输出：父线程设置的值
```

------

##### 实现原理

InheritableThreadLocal 源码：

```java
public class InheritableThreadLocal<T> extends ThreadLocal<T> {
    protected T childValue(T parentValue) {
        return parentValue;
    }
    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}
```

实现父子线程间的局部变量共享需要追溯到 Thread 对象的构造方法：

```java
private void init(ThreadGroup g, Runnable target, String name, long stackSize, AccessControlContext acc,
                  // 该参数默认是 true
                  boolean inheritThreadLocals) {
  	// ...
    Thread parent = currentThread();

    // 判断父线程（创建子线程的线程）的 inheritableThreadLocals 属性不为 null
    if (inheritThreadLocals && parent.inheritableThreadLocals != null) {
        // 复制父线程的 inheritableThreadLocals 属性，实现父子线程局部变量共享
        this.inheritableThreadLocals = ThreadLocal.createInheritedMap(parent.inheritableThreadLocals); 
    }
    // ..
}
// 【本质上还是创建 ThreadLocalMap，只是把父类中的可继承数据设置进去了】
static ThreadLocalMap createInheritedMap(ThreadLocalMap parentMap) {
    return new ThreadLocalMap(parentMap);
}
private ThreadLocalMap(ThreadLocalMap parentMap) {
    // 获取父线程的哈希表
    Entry[] parentTable = parentMap.table;
    int len = parentTable.length;
    setThreshold(len);
    table = new Entry[len];
	// 【逐个复制父线程 ThreadLocalMap 中的数据】
    for (int j = 0; j < len; j++) {
        Entry e = parentTable[j];
        if (e != null) {
            ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
            if (key != null) {
                // 调用的是 InheritableThreadLocal#childValue(T parentValue)
                Object value = key.childValue(e.value);
                Entry c = new Entry(key, value);
                int h = key.threadLocalHashCode & (len - 1);
                // 线性探测
                while (table[h] != null)
                    h = nextIndex(h, len);
                table[h] = c;
                size++;
            }
        }
    }
}
```

参考文章：https://blog.csdn.net/feichitianxia/article/details/110495764

------

## 线程池

### 基本概述

线程池：一个容纳多个线程的容器，容器中的线程可以重复使用，省去了频繁创建和销毁线程对象的操作

线程池作用：

1. 降低资源消耗，减少了创建和销毁线程的次数，每个工作线程都可以被重复利用，可执行多个任务
2. 提高响应速度，当任务到达时，如果有线程可以直接用，不会出现系统僵死
3. 提高线程的可管理性，如果无限制的创建线程，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控

线程池的核心思想：**线程复用**，同一个线程可以被重复使用，来处理多个任务

池化技术 (Pool) ：一种编程技巧，核心思想是资源复用，在请求量大时能优化应用性能，降低系统频繁建连的资源开销

------

### 阻塞队列

#### 基本介绍

有界队列和无界队列：

- 有界队列：有固定大小的队列，比如设定了固定大小的 LinkedBlockingQueue，又或者大小为 0
- 无界队列：没有设置固定大小的队列，这些队列可以直接入队，直到溢出（超过 Integer.MAX_VALUE），所以相当于无界

java.util.concurrent.BlockingQueue 接口有以下阻塞队列的实现：**FIFO 队列**

- ArrayBlockQueue：由数组结构组成的有界阻塞队列
- LinkedBlockingQueue：由链表结构组成的无界（默认大小 Integer.MAX_VALUE）的阻塞队列
- PriorityBlockQueue：支持优先级排序的无界阻塞队列
- DelayedWorkQueue：使用优先级队列实现的延迟无界阻塞队列
- SynchronousQueue：不存储元素的阻塞队列，每一个生产线程会阻塞到有一个 put 的线程放入元素为止
- LinkedTransferQueue：由链表结构组成的无界阻塞队列
- LinkedBlockingDeque：由链表结构组成的**双向**阻塞队列

与普通队列（LinkedList、ArrayList等）的不同点在于阻塞队列中阻塞添加和阻塞删除方法，以及线程安全：

- 阻塞添加 put()：当阻塞队列元素已满时，添加队列元素的线程会被阻塞，直到队列元素不满时才重新唤醒线程执行
- 阻塞删除 take()：在队列元素为空时，删除队列元素的线程将被阻塞，直到队列不为空再执行删除操作（一般会返回被删除的元素)

------

#### 核心方法

| 方法类型         | 抛出异常  | 特殊值   | 阻塞   | 超时               |
| ---------------- | --------- | -------- | ------ | ------------------ |
| 插入（尾）       | add(e)    | offer(e) | put(e) | offer(e,time,unit) |
| 移除（头）       | remove()  | poll()   | take() | poll(time,unit)    |
| 检查（队首元素） | element() | peek()   | 不可用 | 不可用             |

- 抛出异常组：
  - 当阻塞队列满时：在往队列中 add 插入元素会抛出 IIIegalStateException: Queue full
  - 当阻塞队列空时：再往队列中 remove 移除元素，会抛出 NoSuchException
- 特殊值组：
  - 插入方法：成功 true，失败 false
  - 移除方法：成功返回出队列元素，队列没有就返回 null
- 阻塞组：
  - 当阻塞队列满时，生产者继续往队列里 put 元素，队列会一直阻塞生产线程直到队列有空间 put 数据或响应中断退出
  - 当阻塞队列空时，消费者线程试图从队列里 take 元素，队列会一直阻塞消费者线程直到队列中有可用元素
- 超时退出：当阻塞队列满时，队里会阻塞生产者线程一定时间，超过限时后生产者线程会退出

------

#### 链表队列

##### 入队出队

LinkedBlockingQueue 源码：

```java
public class LinkedBlockingQueue<E> extends AbstractQueue<E>
			implements BlockingQueue<E>, java.io.Serializable {
	static class Node<E> {
        E item;
        /**
        * 下列三种情况之一
        * - 真正的后继节点
        * - 自己, 发生在出队时
        * - null, 表示是没有后继节点, 是尾节点了
        */
        Node<E> next;

        Node(E x) { item = x; }
    }
}
```

入队：**尾插法**

- 初始化链表 `last = head = new Node<E>(null)`，**Dummy 节点用来占位**，item 为 null

  ```java
  public LinkedBlockingQueue(int capacity) {
      // 默认是 Integer.MAX_VALUE
      if (capacity <= 0) throw new IllegalArgumentException();
      this.capacity = capacity;
      last = head = new Node<E>(null);
  }
  ```

- 当一个节点入队：

  ```java
  private void enqueue(Node<E> node) {
      // 从右向左计算
      last = last.next = node;
  }
  ```

![image-20221221143241372](JUC.assets/image-20221221143241372.png)

- 再来一个节点入队 `last = last.next = node`

出队：**出队头节点**，FIFO

- 出队源码：

  ```java
  private E dequeue() {
      Node<E> h = head;
      // 获取临头节点
      Node<E> first = h.next;
      // 自己指向自己，help GC
      h.next = h;
      head = first;
      // 出队的元素
      E x = first.item;
      // 【当前节点置为 Dummy 节点】
      first.item = null;
      return x;
  }
  ```

- `h = head` → `first = h.next`

![image-20221221143402376](JUC.assets/image-20221221143402376.png)

* `h.next = h` → `head = first`

![image-20221221144624353](JUC.assets/image-20221221144624353.png)

- `first.item = null`：当前节点置为 Dummy 节点

------

##### 加锁分析

用了两把锁和 dummy 节点：

- 用一把锁，同一时刻，最多只允许有一个线程（生产者或消费者，二选一）执行
- 用两把锁，同一时刻，可以允许两个线程同时（一个生产者与一个消费者）执行
  - 消费者与消费者线程仍然串行
  - 生产者与生产者线程仍然串行

线程安全分析：

- 当节点总数大于 2 时（包括 dummy 节点），**putLock 保证的是 last 节点的线程安全，takeLock 保证的是 head 节点的线程安全**，两把锁保证了入队和出队没有竞争

- 当节点总数等于 2 时（即一个 dummy 节点，一个正常节点）这时候，仍然是两把锁锁两个对象，不会竞争

- 当节点总数等于 1 时（就一个 dummy 节点）这时 take 线程会被 notEmpty 条件阻塞，有竞争，会阻塞

  ```java
  // 用于 put(阻塞) offer(非阻塞)
  private final ReentrantLock putLock = new ReentrantLock();
  private final Condition notFull = putLock.newCondition();	// 阻塞等待不满，说明已经满了
  
  // 用于 take(阻塞) poll(非阻塞)
  private final ReentrantLock takeLock = new ReentrantLock();
  private final Condition notEmpty = takeLock.newCondition();	// 阻塞等待不空，说明已经是空的
  ```

入队出队：

- put 操作：

  ```java
  public void put(E e) throws InterruptedException {
      // 空指针异常
      if (e == null) throw new NullPointerException();
      int c = -1;
      // 把待添加的元素封装为 node 节点
      Node<E> node = new Node<E>(e);
      // 获取全局生产锁
      final ReentrantLock putLock = this.putLock;
      // count 用来维护元素计数
      final AtomicInteger count = this.count;
      // 获取可打断锁，会抛出异常
      putLock.lockInterruptibly();
      try {
      	// 队列满了等待
          while (count.get() == capacity) {
              // 【等待队列不满时，就可以生产数据】，线程处于 Waiting
              notFull.await();
          }
          // 有空位, 入队且计数加一，尾插法
          enqueue(node);
          // 返回自增前的数字
          c = count.getAndIncrement();
          // put 完队列还有空位, 唤醒其他生产 put 线程，唤醒一个减少竞争
          if (c + 1 < capacity)
              notFull.signal();
      } finally {
          // 解锁
          putLock.unlock();
      }
      // c自增前是0，说明生产了一个元素，唤醒一个 take 线程
      if (c == 0)
          signalNotEmpty();
  }
  ```

  ```java
  private void signalNotEmpty() {
      final ReentrantLock takeLock = this.takeLock;
      takeLock.lock();
      try {
          // 调用 notEmpty.signal()，而不是 notEmpty.signalAll() 是为了减少竞争，因为只剩下一个元素
          notEmpty.signal();
      } finally {
          takeLock.unlock();
      }
  }
  ```

- take 操作：

  ```java
  public E take() throws InterruptedException {
      E x;
      int c = -1;
      // 元素个数
      final AtomicInteger count = this.count;
      // 获取全局消费锁
      final ReentrantLock takeLock = this.takeLock;
      // 可打断锁
      takeLock.lockInterruptibly();
      try {
          // 没有元素可以出队
          while (count.get() == 0) {
              // 【阻塞等待队列不空，就可以消费数据】，线程处于 Waiting
              notEmpty.await();
          }
          // 出队，计数减一，FIFO，出队头节点
          x = dequeue();
          // 返回自减前的数字
          c = count.getAndDecrement();
          // 队列还有元素
          if (c > 1)
              // 唤醒一个消费take线程
              notEmpty.signal();
      } finally {
          takeLock.unlock();
      }
      // c 是消费前的数据，消费前满了，消费一个后还剩一个空位，唤醒生产线程
      if (c == capacity)
          // 调用的是 notFull.signal() 而不是 notFull.signalAll() 是为了减少竞争
          signalNotFull();
      return x;
  }
  ```

------

##### 性能比较

主要列举 LinkedBlockingQueue 与 ArrayBlockingQueue 的性能比较：

- Linked 支持有界，Array 强制有界
- Linked 实现是链表，Array 实现是数组
- Linked 是懒惰的，而 Array 需要提前初始化 Node 数组
- Linked 每次入队会生成新 Node，而 Array 的 Node 是提前创建好的
- Linked 两把锁，Array 一把锁

------

#### 同步队列

##### 成员属性

SynchronousQueue 是一个不存储元素的 BlockingQueue，**每一个生产者必须阻塞匹配到一个消费者**

成员变量：

- 运行当前程序的平台拥有 CPU 的数量：

  ```java
  static final int NCPUS = Runtime.getRuntime().availableProcessors()
  ```

- 指定超时时间后，当前线程最大自旋次数：

  ```java
  // 只有一个 CPU 时自旋次数为 0，所有程序都是串行执行，多核 CPU 时自旋 32 次是一个经验值
  static final int maxTimedSpins = (NCPUS < 2) ? 0 : 32;
  ```

  自旋的原因：线程挂起唤醒需要进行上下文切换，涉及到用户态和内核态的转变，是非常消耗资源的。自旋期间线程会一直检查自己的状态是否被匹配到，如果自旋期间被匹配到，那么直接就返回了，如果自旋次数达到某个指标后，还是会将当前线程挂起

- 未指定超时时间，当前线程最大自旋次数：

  ```java
  static final int maxUntimedSpins = maxTimedSpins * 16;	// maxTimedSpins 的 16 倍
  ```

- 指定超时限制的阈值，小于该值的线程不会被挂起：

  ```java
  static final long spinForTimeoutThreshold = 1000L;	// 纳秒
  ```

  超时时间设置的小于该值，就会被禁止挂起，阻塞再唤醒的成本太高，不如选择自旋空转

- 转换器：

  ```java
  private transient volatile Transferer<E> transferer;
  abstract static class Transferer<E> {
      /**
      * 参数一：可以为 null，null 时表示这个请求是一个 REQUEST 类型的请求，反之是一个 DATA 类型的请求
      * 参数二：如果为 true 表示指定了超时时间，如果为 false 表示不支持超时，会一直阻塞到匹配或者被打断
      * 参数三：超时时间限制，单位是纳秒
      
      * 返回值：返回值如果不为 null 表示匹配成功，DATA 类型的请求返回当前线程 put 的数据
      * 	     如果返回 null，表示请求超时或被中断
      */
      abstract E transfer(E e, boolean timed, long nanos);
  }
  ```

- 构造方法：

  ```java
  public SynchronousQueue(boolean fair) {
      // fair 默认 false
      // 非公平模式实现的数据结构是栈，公平模式的数据结构是队列
      transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
  }
  ```

- 成员方法：

  ```java
  public boolean offer(E e) {
      if (e == null) throw new NullPointerException();
      return transferer.transfer(e, true, 0) != null;
  }
  public E poll() {
      return transferer.transfer(null, true, 0);
  }
  ```

------

##### 非公实现

TransferStack 是非公平的同步队列，因为所有的请求都被压入栈中，栈顶的元素会最先得到匹配，造成栈底的等待线程饥饿

TransferStack 类成员变量：

- 请求类型：

  ```java
  // 表示 Node 类型为请求类型
  static final int REQUEST    = 0;
  // 表示 Node类 型为数据类型
  static final int DATA       = 1;
  // 表示 Node 类型为匹配中类型
  // 假设栈顶元素为 REQUEST-NODE，当前请求类型为 DATA，入栈会修改类型为 FULFILLING 【栈顶 & 栈顶之下的一个node】
  // 假设栈顶元素为 DATA-NODE，当前请求类型为 REQUEST，入栈会修改类型为 FULFILLING 【栈顶 & 栈顶之下的一个node】
  static final int FULFILLING = 2;
  ```

- 栈顶元素：

  ```java
  volatile SNode head;
  ```

内部类 SNode：

- 成员变量：

  ```java
  static final class SNode {
      // 指向下一个栈帧
      volatile SNode next; 
      // 与当前 node 匹配的节点
      volatile SNode match;
      // 假设当前node对应的线程自旋期间未被匹配成功，那么node对应的线程需要挂起，
      // 挂起前 waiter 保存对应的线程引用，方便匹配成功后，被唤醒。
      volatile Thread waiter;
      
      // 数据域，不为空表示当前 Node 对应的请求类型为 DATA 类型，反之则表示 Node 为 REQUEST 类型
      Object item; 
      // 表示当前Node的模式 【DATA/REQUEST/FULFILLING】
      int mode;
  }
  ```

- 构造方法：

  ```java
  SNode(Object item) {
      this.item = item;
  }
  ```

- 设置方法：设置 Node 对象的 next 字段，此处**对 CAS 进行了优化**，提升了 CAS 的效率

  ```java
  boolean casNext(SNode cmp, SNode val) {
      //【优化：cmp == next】，可以提升一部分性能。 cmp == next 不相等，就没必要走 cas指令。
      return cmp == next && UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
  }
  ```

- 匹配方法：

  ```java
  boolean tryMatch(SNode s) {
      // 当前 node 尚未与任何节点发生过匹配，CAS 设置 match 字段为 s 节点，表示当前 node 已经被匹配
      if (match == null && UNSAFE.compareAndSwapObject(this, matchOffset, null, s)) {
          // 当前 node 如果自旋结束，会 park 阻塞，阻塞前将 node 对应的 Thread 保留到 waiter 字段
          // 获取当前 node 对应的阻塞线程
          Thread w = waiter;
          // 条件成立说明 node 对应的 Thread 正在阻塞
          if (w != null) {
              waiter = null;
              // 使用 unpark 方式唤醒线程
              LockSupport.unpark(w);
          }
          return true;
      }
      // 匹配成功返回 true
      return match == s;
  }
  ```

- 取消方法：

  ```java
  // 取消节点的方法
  void tryCancel() {
      // match 字段指向自己，表示这个 node 是取消状态，取消状态的 node，最终会被强制移除出栈
      UNSAFE.compareAndSwapObject(this, matchOffset, null, this);
  }
  
  boolean isCancelled() {
      return match == this;
  }
  ```

TransferStack 类成员方法：

- snode()：填充节点方法

  ```java
  static SNode snode(SNode s, Object e, SNode next, int mode) {
      // 引用指向空时，snode 方法会创建一个 SNode 对象 
      if (s == null) s = new SNode(e);
      // 填充数据
      s.mode = mode;
      s.next = next;
      return s;
  }
  ```

- transfer()：核心方法，请求匹配出栈，不匹配阻塞

  ```java
  E transfer(E e, boolean timed, long nanos) {
  	// 包装当前线程的 node
      SNode s = null;
      // 根据元素判断当前的请求类型
      int mode = (e == null) ? REQUEST : DATA;
  	// 自旋
      for (;;) {
          // 获取栈顶指针
          SNode h = head;
         // 【CASE1】：当前栈为空或者栈顶 node 模式与当前请求模式一致无法匹配，做入栈操作
          if (h == null || h.mode == mode) {
              // 当前请求是支持超时的，但是 nanos <= 0 说明这个请求不支持 “阻塞等待”
              if (timed && nanos <= 0) { 
                  // 栈顶元素是取消状态
                  if (h != null && h.isCancelled())
                      // 栈顶出栈，设置新的栈顶
                      casHead(h, h.next);
                  else
                      // 表示【匹配失败】
                      return null;
              // 入栈
              } else if (casHead(h, s = snode(s, e, h, mode))) {
                  // 等待被匹配的逻辑，正常情况返回匹配的节点；取消情况返回当前节点，就是 s
                  SNode m = awaitFulfill(s, timed, nanos);
                  // 说明当前 node 是【取消状态】
                  if (m == s) { 
                      // 将取消节点出栈
                      clean(s);
                      return null;
                  }
                  // 执行到这说明【匹配成功】了
                  // 栈顶有节点并且 匹配节点还未出栈，需要协助出栈
                  if ((h = head) != null && h.next == s)
                      casHead(h, s.next);
                  // 当前 node 模式为 REQUEST 类型，返回匹配节点的 m.item 数据域
                  // 当前 node 模式为 DATA 类型：返回 node.item 数据域，当前请求提交的数据 e
                  return (E) ((mode == REQUEST) ? m.item : s.item);
              }
          // 【CASE2】：逻辑到这说明请求模式不一致，如果栈顶不是 FULFILLING 说明没被其他节点匹配，【当前可以匹配】
          } else if (!isFulfilling(h.mode)) {
              // 头节点是取消节点，match 指向自己，协助出栈
              if (h.isCancelled())
                  casHead(h, h.next);
              // 入栈当前请求的节点
              else if (casHead(h, s=snode(s, e, h, FULFILLING|mode))) {
                  for (;;) { 
                      // m 是 s 的匹配的节点
                      SNode m = s.next;
                      // m 节点在 awaitFulfill 方法中被中断，clean 了自己
                      if (m == null) {
                          // 清空栈
                          casHead(s, null);
                          s = null;
                          // 返回到外层自旋中
                          break;
                      }
                      // 获取匹配节点的下一个节点
                      SNode mn = m.next;
                      // 尝试匹配，【匹配成功】，则将 fulfilling 和 m 一起出栈，并且唤醒被匹配的节点的线程
                      if (m.tryMatch(s)) {
                          casHead(s, mn);
                          return (E) ((mode == REQUEST) ? m.item : s.item);
                      } else
                          // 匹配失败，出栈 m
                          s.casNext(m, mn);
                  }
              }
          // 【CASE3】：栈顶模式为 FULFILLING 模式，表示【栈顶和栈顶下面的节点正在发生匹配】，当前请求需要做协助工作
          } else {
              // h 表示的是 fulfilling 节点，m 表示 fulfilling 匹配的节点
              SNode m = h.next;
              if (m == null)
                  // 清空栈
                  casHead(h, null);
              else {
                  SNode mn = m.next;
                  // m 和 h 匹配，唤醒 m 中的线程
                  if (m.tryMatch(h))
                      casHead(h, mn);
                  else
                      h.casNext(m, mn);
              }
          }
      }
  }
  ```

- awaitFulfill()：阻塞当前线程等待被匹配，返回匹配的节点，或者被取消的节点

  ```java
  SNode awaitFulfill(SNode s, boolean timed, long nanos) {
      // 等待的截止时间
      final long deadline = timed ? System.nanoTime() + nanos : 0L;
      // 当前线程
      Thread w = Thread.currentThread();
      // 表示当前请求线程在下面的 for(;;) 自旋检查的次数
      int spins = (shouldSpin(s) ? (timed ? maxTimedSpins : maxUntimedSpins) : 0);
      // 自旋检查逻辑：是否匹配、是否超时、是否被中断
      for (;;) {
          // 当前线程收到中断信号，需要设置 node 状态为取消状态
          if (w.isInterrupted())
              s.tryCancel();
          // 获取与当前 s 匹配的节点
          SNode m = s.match;
          if (m != null)
              // 可能是正常的匹配的，也可能是取消的
              return m;
          // 执行了超时限制就判断是否超时
          if (timed) {
              nanos = deadline - System.nanoTime();
              // 【超时了，取消节点】
              if (nanos <= 0L) {
                  s.tryCancel();
                  continue;
              }
          }
          // 说明当前线程还可以进行自旋检查
          if (spins > 0)
              // 自旋一次 递减 1
              spins = shouldSpin(s) ? (spins - 1) : 0;
          // 说明没有自旋次数了
          else if (s.waiter == null)
              //【把当前 node 对应的 Thread 保存到 node.waiter 字段中，要阻塞了】
              s.waiter = w;
          // 没有超时限制直接阻塞
          else if (!timed)
              LockSupport.park(this);
          // nanos > 1000 纳秒的情况下，才允许挂起当前线程
          else if (nanos > spinForTimeoutThreshold)
              LockSupport.parkNanos(this, nanos);
      }
  }
  ```

  ```java
  boolean shouldSpin(SNode s) {
      // 获取栈顶
      SNode h = head;
      // 条件一成立说明当前 s 就是栈顶，允许自旋检查
      // 条件二成立说明当前 s 节点自旋检查期间，又来了一个与当前 s 节点匹配的请求，双双出栈后条件会成立
      // 条件三成立前提当前 s 不是栈顶元素，并且当前栈顶正在匹配中，这种状态栈顶下面的元素，都允许自旋检查
      return (h == s || h == null || isFulfilling(h.mode));
  }
  ```

- clear()：指定节点出栈

  ```java
  void clean(SNode s) {
      // 清空数据域和关联线程
      s.item = null;
      s.waiter = null;
      
  	// 获取取消节点的下一个节点
      SNode past = s.next;
      // 判断后继节点是不是取消节点，是就更新 past
      if (past != null && past.isCancelled())
          past = past.next;
  
      SNode p;
      // 从栈顶开始向下检查，【将栈顶开始向下的 取消状态 的节点全部清理出去】，直到碰到 past 或者不是取消状态为止
      while ((p = head) != null && p != past && p.isCancelled())
          // 修改的是内存地址对应的值，p 指向该内存地址所以数据一直在变化
          casHead(p, p.next);
  	// 说明中间遇到了不是取消状态的节点，继续迭代下去
      while (p != null && p != past) {
          SNode n = p.next;
          if (n != null && n.isCancelled())
              p.casNext(n, n.next);
          else
              p = n;
      }
  }
  ```

------

##### 公平实现

TransferQueue 是公平的同步队列，采用 FIFO 的队列实现，请求节点与队尾模式不同，需要与队头发生匹配

TransferQueue 类成员变量：

- 指向队列的 dummy 节点：

  ```java
  transient volatile QNode head;
  ```

- 指向队列的尾节点：

  ```java
  transient volatile QNode tail;
  ```

- 被清理节点的前驱节点：

  ```java
  transient volatile QNode cleanMe;
  ```

  入队操作是两步完成的，第一步是 t.next = newNode，第二步是 tail = newNode，所以队尾节点出队，是一种非常特殊的情况

TransferQueue 内部类：

- QNode：

  ```java
  static final class QNode {
      // 指向当前节点的下一个节点
      volatile QNode next;
      // 数据域，Node 代表的是 DATA 类型 item 表示数据，否则 Node 代表的 REQUEST 类型，item == null
      volatile Object item;
      // 假设当前 node 对应的线程自旋期间未被匹配成功，那么 node 对应的线程需要挂起，
      // 挂起前 waiter 保存对应的线程引用，方便匹配成功后被唤醒。
      volatile Thread waiter;
      // true 当前 Node 是一个 DATA 类型，false 表示当前 Node 是一个 REQUEST 类型
      final boolean isData;
  
  	// 构建方法
      QNode(Object item, boolean isData) {
          this.item = item;
          this.isData = isData;
      }
  
      // 尝试取消当前 node，取消状态的 node 的 item 域指向自己
      void tryCancel(Object cmp) {
          UNSAFE.compareAndSwapObject(this, itemOffset, cmp, this);
      }
  
      // 判断当前 node 是否为取消状态
      boolean isCancelled() {
          return item == this;
      }
  
      // 判断当前节点是否 “不在” 队列内，当 next 指向自己时，说明节点已经出队。
      boolean isOffList() {
          return next == this;
      }
  }
  ```

TransferQueue 类成员方法：

- 设置头尾节点：

  ```java
  void advanceHead(QNode h, QNode nh) {
      // 设置头指针指向新的节点，
      if (h == head && UNSAFE.compareAndSwapObject(this, headOffset, h, nh))
          // 老的头节点出队
          h.next = h;
  }
  void advanceTail(QNode t, QNode nt) {
      if (tail == t)
          // 更新队尾节点为新的队尾
          UNSAFE.compareAndSwapObject(this, tailOffset, t, nt);
  }
  ```

- transfer()：核心方法

  ```java
  E transfer(E e, boolean timed, long nanos) {
      // s 指向当前请求对应的 node
      QNode s = null;
      // 是否是 DATA 类型的请求
      boolean isData = (e != null);
  	// 自旋
      for (;;) {
          QNode t = tail;
          QNode h = head;
          if (t == null || h == null)
              continue;
  		// head 和 tail 同时指向 dummy 节点，说明是空队列
          // 队尾节点与当前请求类型是一致的情况，说明阻塞队列中都无法匹配，
          if (h == t || t.isData == isData) {
              // 获取队尾 t 的 next 节点
              QNode tn = t.next;
              // 多线程环境中其他线程可能修改尾节点
              if (t != tail)
                  continue;
              // 已经有线程入队了，更新 tail
              if (tn != null) {
                  advanceTail(t, tn);
                  continue;
              }
              // 允许超时，超时时间小于 0，这种方法不支持阻塞等待
              if (timed && nanos <= 0)
                  return null;
              // 创建 node 的逻辑
              if (s == null)
                  s = new QNode(e, isData);
              // 将 node 添加到队尾
              if (!t.casNext(null, s))
                  continue;
  			// 更新队尾指针
              advanceTail(t, s);
              
              // 当前节点 等待匹配....
              Object x = awaitFulfill(s, e, timed, nanos);
              
              // 说明【当前 node 状态为 取消状态】，需要做出队逻辑
              if (x == s) {
                  clean(t, s);
                  return null;
              }
  			// 说明当前 node 仍然在队列内，匹配成功，需要做出队逻辑
              if (!s.isOffList()) {
                  // t 是当前 s 节点的前驱节点，判断 t 是不是头节点，是就更新 dummy 节点为 s 节点
                  advanceHead(t, s);
                  // s 节点已经出队，所以需要把它的 item 域设置为它自己，表示它是个取消状态
                  if (x != null)
                      s.item = s;
                  s.waiter = null;
              }
              return (x != null) ? (E)x : e;
  		// 队尾节点与当前请求节点【互补匹配】
          } else {
              // h.next 节点，【请求节点与队尾模式不同，需要与队头发生匹配】，TransferQueue 是一个【公平模式】
              QNode m = h.next;
              // 并发导致其他线程修改了队尾节点，或者已经把 head.next 匹配走了
              if (t != tail || m == null || h != head)
                  continue;
  			// 获取匹配节点的数据域保存到 x
              Object x = m.item;
              // 判断是否匹配成功
              if (isData == (x != null) ||
                  x == m ||
                  !m.casItem(x, e)) {
                  advanceHead(h, m);
                  continue;
              }
  			// 【匹配完成】，将头节点出队，让这个新的头结点成为 dummy 节点
              advanceHead(h, m);
              // 唤醒该匹配节点的线程
              LockSupport.unpark(m.waiter);
              return (x != null) ? (E)x : e;
          }
      }
  }
  ```

- awaitFulfill()：阻塞当前线程等待被匹配

  ```java
  Object awaitFulfill(QNode s, E e, boolean timed, long nanos) {
      // 表示等待截止时间
      final long deadline = timed ? System.nanoTime() + nanos : 0L;
      Thread w = Thread.currentThread();
      // 自选检查的次数
      int spins = ((head.next == s) ? (timed ? maxTimedSpins : maxUntimedSpins) : 0);
      for (;;) {
          // 被打断就取消节点
          if (w.isInterrupted())
              s.tryCancel(e);
          // 获取当前 Node 数据域
          Object x = s.item;
          
          // 当前请求为 DATA 模式时：e 请求带来的数据
          // s.item 修改为 this，说明当前 QNode 对应的线程 取消状态
          // s.item 修改为 null 表示已经有匹配节点了，并且匹配节点拿走了 item 数据
  
          // 当前请求为 REQUEST 模式时：e == null
          // s.item 修改为 this，说明当前 QNode 对应的线程 取消状态
          // s.item != null 且 item != this  表示当前 REQUEST 类型的 Node 已经匹配到 DATA 了 
          if (x != e)
              return x;
          // 超时检查
          if (timed) {
              nanos = deadline - System.nanoTime();
              if (nanos <= 0L) {
                  s.tryCancel(e);
                  continue;
              }
          }
          // 自旋次数减一
          if (spins > 0)
              --spins;
          // 没有自旋次数了，把当前线程封装进去 waiter
          else if (s.waiter == null)
              s.waiter = w;
          // 阻塞
          else if (!timed)
              LockSupport.park(this);
          else if (nanos > spinForTimeoutThreshold)
              LockSupport.parkNanos(this, nanos);
      }
  }
  ```

------

### 操作Pool

#### 创建方式

##### Executor

存放线程的容器：

```java
private final HashSet<Worker> workers = new HashSet<Worker>();
```

构造方法：

```java
public ThreadPoolExecutor(int corePoolSize,
                          int maximumPoolSize,
                          long keepAliveTime,
                          TimeUnit unit,
                          BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler)
```

参数介绍：

- corePoolSize：核心线程数，定义了最小可以同时运行的线程数量

- maximumPoolSize：最大线程数，当队列中存放的任务达到队列容量时，当前可以同时运行的数量变为最大线程数，创建线程并立即执行最新的任务，与核心线程数之间的差值又叫救急线程数

- keepAliveTime：救急线程最大存活时间，当线程池中的线程数量大于 `corePoolSize` 的时候，如果这时没有新的任务提交，核心线程外的线程不会立即销毁，而是会等到 `keepAliveTime` 时间超过销毁

- unit：`keepAliveTime` 参数的时间单位

- workQueue：阻塞队列，存放被提交但尚未被执行的任务

- threadFactory：线程工厂，创建新线程时用到，可以为线程创建时起名字

- handler：拒绝策略，线程到达最大线程数仍有新任务时会执行拒绝策略

  RejectedExecutionHandler 下有 4 个实现类：

  - AbortPolicy：让调用者抛出 RejectedExecutionException 异常，**默认策略**
  - CallerRunsPolicy：让调用者运行的调节机制，将某些任务回退到调用者，从而降低新任务的流量
  - DiscardPolicy：直接丢弃任务，不予任何处理也不抛出异常
  - DiscardOldestPolicy：放弃队列中最早的任务，把当前任务加入队列中尝试再次提交当前任务

  补充：其他框架拒绝策略

  - Dubbo：在抛出 RejectedExecutionException 异常前记录日志，并 dump 线程栈信息，方便定位问题
  - Netty：创建一个新线程来执行任务
  - ActiveMQ：带超时等待（60s）尝试放入队列
  - PinPoint：它使用了一个拒绝策略链，会逐一尝试策略链中每种拒绝策略

工作原理：

![image-20221221151417066](JUC.assets/image-20221221151417066.png)

1. 创建线程池，这时没有创建线程（**懒惰**），等待提交过来的任务请求，调用 execute 方法才会创建线程
2. 当调用 execute() 方法添加一个请求任务时，线程池会做如下判断：
   - 如果正在运行的线程数量小于 corePoolSize，那么马上创建线程运行这个任务
   - 如果正在运行的线程数量大于或等于 corePoolSize，那么将这个任务放入队列
   - 如果这时队列满了且正在运行的线程数量还小于 maximumPoolSize，那么会创建非核心线程**立刻运行这个任务**，对于阻塞队列中的任务不公平。这是因为创建每个 Worker（线程）对象会绑定一个初始任务，启动 Worker 时会优先执行
   - 如果队列满了且正在运行的线程数量大于或等于 maximumPoolSize，那么线程池会启动饱和**拒绝策略**来执行
3. 当一个线程完成任务时，会从队列中取下一个任务来执行
4. 当一个线程空闲超过一定的时间（keepAliveTime）时，线程池会判断：如果当前运行的线程数大于 corePoolSize，那么这个线程就被停掉，所以线程池的所有任务完成后最终会收缩到 corePoolSize 大小

图片来源：https://space.bilibili.com/457326371/

------

##### Executors

Executors 提供了四种线程池的创建：newCachedThreadPool、newFixedThreadPool、newSingleThreadExecutor、newScheduledThreadPool

- newFixedThreadPool：创建一个拥有 n 个线程的线程池

  ```java
  public static ExecutorService newFixedThreadPool(int nThreads) {
      return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>());
  }
  ```

  - 核心线程数 == 最大线程数（没有救急线程被创建），因此也无需超时时间
  - LinkedBlockingQueue 是一个单向链表实现的阻塞队列，默认大小为 `Integer.MAX_VALUE`，也就是无界队列，可以放任意数量的任务，在任务比较多的时候会导致 OOM（内存溢出）
  - 适用于任务量已知，相对耗时的长期任务

- newCachedThreadPool：创建一个可扩容的线程池

  ```java
  public static ExecutorService newCachedThreadPool() {
      return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                                    new SynchronousQueue<Runnable>());
  }
  ```

  - 核心线程数是 0， 最大线程数是 29 个 1，全部都是救急线程（60s 后可以回收），可能会创建大量线程，从而导致 **OOM**
  - SynchronousQueue 作为阻塞队列，没有容量，对于每一个 take 的线程会阻塞直到有一个 put 的线程放入元素为止（类似一手交钱、一手交货）
  - 适合任务数比较密集，但每个任务执行时间较短的情况

- newSingleThreadExecutor：创建一个只有 1 个线程的单线程池

  ```java
  public static ExecutorService newSingleThreadExecutor() {
      return new FinalizableDelegatedExecutorService
          (new ThreadPoolExecutor(1, 1,0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>()));
  }
  ```

  - 保证所有任务按照**指定顺序执行**，线程数固定为 1，任务数多于 1 时会放入无界队列排队，任务执行完毕，这唯一的线程也不会被释放

对比：

- 创建一个单线程串行执行任务，如果任务执行失败而终止那么没有任何补救措施，线程池会新建一个线程，保证池的正常工作

- Executors.newSingleThreadExecutor() 线程个数始终为 1，不能修改。FinalizableDelegatedExecutorService 应用的是装饰器模式，只对外暴露了 ExecutorService 接口，因此不能调用 ThreadPoolExecutor 中特有的方法

  原因：父类不能直接调用子类中的方法，需要反射或者创建对象的方式，可以调用子类静态方法

- Executors.newFixedThreadPool(1) 初始时为 1，可以修改。对外暴露的是 ThreadPoolExecutor 对象，可以强转后调用 setCorePoolSize 等方法进行修改

![image-20221221151442255](JUC.assets/image-20221221151442255.png)

------

##### 开发要求

阿里巴巴 Java 开发手册要求：

- **线程资源必须通过线程池提供，不允许在应用中自行显式创建线程**

  - 使用线程池的好处是减少在创建和销毁线程上所消耗的时间以及系统资源的开销，解决资源不足的问题
  - 如果不使用线程池，有可能造成系统创建大量同类线程而导致消耗完内存或者过度切换的问题

- 线程池不允许使用 Executors 去创建，而是通过 ThreadPoolExecutor 的方式，这样的处理方式更加明确线程池的运行规则，规避资源耗尽的风险

  Executors 返回的线程池对象弊端如下：

  - FixedThreadPool 和 SingleThreadPool：请求队列长度为 Integer.MAX_VALUE，可能会堆积大量的请求，从而导致 OOM
  - CacheThreadPool 和 ScheduledThreadPool：允许创建线程数量为 Integer.MAX_VALUE，可能会创建大量的线程，导致 OOM

创建多大容量的线程池合适？

- 一般来说池中**总线程数是核心池线程数量两倍**，确保当核心池有线程停止时，核心池外有线程进入核心池

- 过小会导致程序不能充分地利用系统资源、容易导致饥饿

- 过大会导致更多的线程上下文切换，占用更多内存

  上下文切换：当前任务在执行完 CPU 时间片切换到另一个任务之前会先保存自己的状态，以便下次再切换回这个任务时，可以再加载这个任务的状态，任务从保存到再加载的过程就是一次上下文切换

核心线程数常用公式：

- **CPU 密集型任务 (N+1)：** 这种任务消耗的是 CPU 资源，可以将核心线程数设置为 N (CPU 核心数) + 1，比 CPU 核心数多出来的一个线程是为了防止线程发生缺页中断，或者其它原因导致的任务暂停而带来的影响。一旦任务暂停，CPU 某个核心就会处于空闲状态，而在这种情况下多出来的一个线程就可以充分利用 CPU 的空闲时间

  CPU 密集型简单理解就是利用 CPU 计算能力的任务比如在内存中对大量数据进行分析

- **I/O 密集型任务：** 这种系统 CPU 处于阻塞状态，用大部分的时间来处理 I/O 交互，而线程在处理 I/O 的时间段内不会占用 CPU 来处理，这时就可以将 CPU 交出给其它线程使用，因此在 I/O 密集型任务的应用中，我们可以多配置一些线程，具体的计算方法是 2N 或 CPU 核数/ (1-阻塞系数)，阻塞系数在 0.8~0.9 之间

  IO 密集型就是涉及到网络读取，文件读取此类任务 ，特点是 CPU 计算耗费时间相比于等待 IO 操作完成的时间来说很少，大部分时间都花在了等待 IO 操作完成上

------

#### 提交方法

ExecutorService 类 API：

| 方法                                                         | 说明                                                         |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| void execute(Runnable command)                               | 执行任务（Executor 类 API）                                  |
| Future<?> submit(Runnable task)                              | 提交任务 task()                                              |
| Future submit(Callable task)                                 | 提交任务 task，用返回值 Future 获得任务执行结果              |
| List<Future> invokeAll(Collection<? extends Callable> tasks) | 提交 tasks 中所有任务                                        |
| List<Future> invokeAll(Collection<? extends Callable> tasks, long timeout, TimeUnit unit) | 提交 tasks 中所有任务，超时时间针对所有task，超时会取消没有执行完的任务，并抛出超时异常 |
| T invokeAny(Collection<? extends Callable> tasks)            | 提交 tasks 中所有任务，哪个任务先成功执行完毕，返回此任务执行结果，其它任务取消 |

execute 和 submit 都属于线程池的方法，对比：

- execute 只能执行 Runnable 类型的任务，没有返回值； submit 既能提交 Runnable 类型任务也能提交 Callable 类型任务，底层是**封装成 FutureTask，然后调用 execute 执行**
- execute 会直接抛出任务执行时的异常，submit 会吞掉异常，可通过 Future 的 get 方法将任务执行时的异常重新抛出

------

#### 关闭方法

ExecutorService 类 API：

| 方法                                                  | 说明                                                         |
| ----------------------------------------------------- | ------------------------------------------------------------ |
| void shutdown()                                       | 线程池状态变为 SHUTDOWN，等待任务执行完后关闭线程池，不会接收新任务，但已提交任务会执行完，而且也可以添加线程（不绑定任务） |
| List shutdownNow()                                    | 线程池状态变为 STOP，用 interrupt 中断正在执行的任务，直接关闭线程池，不会接收新任务，会将队列中的任务返回 |
| boolean isShutdown()                                  | 不在 RUNNING 状态的线程池，此执行者已被关闭，方法返回 true   |
| boolean isTerminated()                                | 线程池状态是否是 TERMINATED，如果所有任务在关闭后完成，返回 true |
| boolean awaitTermination(long timeout, TimeUnit unit) | 调用 shutdown 后，由于调用线程不会等待所有任务运行结束，如果它想在线程池 TERMINATED 后做些事情，可以利用此方法等待 |

------

#### 处理异常

execute 会直接抛出任务执行时的异常，submit 会吞掉异常，有两种处理方法

方法 1：主动捉异常

```java
ExecutorService executorService = Executors.newFixedThreadPool(1);
pool.submit(() -> {
    try {
        System.out.println("task1");
        int i = 1 / 0;
    } catch (Exception e) {
        e.printStackTrace();
    }
});
```

方法 2：使用 Future 对象

```java
ExecutorService executorService = Executors.newFixedThreadPool(1);
Future<?> future = pool.submit(() -> {
    System.out.println("task1");
    int i = 1 / 0;
    return true;
});
System.out.println(future.get());
```

------

### 工作原理

#### 状态信息

ThreadPoolExecutor 使用 int 的**高 3 位来表示线程池状态，低 29 位表示线程数量**。这些信息存储在一个原子变量 ctl 中，目的是将线程池状态与线程个数合二为一，这样就可以用一次 CAS 原子操作进行赋值

- 状态表示：

  ```java
  // 高3位：表示当前线程池运行状态，除去高3位之后的低位：表示当前线程池中所拥有的线程数量
  private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
  // 表示在 ctl 中，低 COUNT_BITS 位，是用于存放当前线程数量的位
  private static final int COUNT_BITS = Integer.SIZE - 3;
  // 低 COUNT_BITS 位所能表达的最大数值，000 11111111111111111111 => 5亿多
  private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
  ```

![image-20221221151520220](JUC.assets/image-20221221151520220.png)

- 四种状态：

  ```java
  // 111 000000000000000000，转换成整数后其实就是一个【负数】
  private static final int RUNNING    = -1 << COUNT_BITS;
  // 000 000000000000000000
  private static final int SHUTDOWN   =  0 << COUNT_BITS;
  // 001 000000000000000000
  private static final int STOP       =  1 << COUNT_BITS;
  // 010 000000000000000000
  private static final int TIDYING    =  2 << COUNT_BITS;
  // 011 000000000000000000
  private static final int TERMINATED =  3 << COUNT_BITS;
  ```

  | 状态       | 高3位 | 接收新任务 | 处理阻塞任务队列 | 说明                                      |
  | ---------- | ----- | ---------- | ---------------- | ----------------------------------------- |
  | RUNNING    | 111   | Y          | Y                |                                           |
  | SHUTDOWN   | 000   | N          | Y                | 不接收新任务，但处理阻塞队列剩余任务      |
  | STOP       | 001   | N          | N                | 中断正在执行的任务，并抛弃阻塞队列任务    |
  | TIDYING    | 010   | -          | -                | 任务全执行完毕，活动线程为 0 即将进入终结 |
  | TERMINATED | 011   | -          | -                | 终止状态                                  |

- 获取当前线程池运行状态：

  ```java
  // ~CAPACITY = ~000 11111111111111111111 = 111 000000000000000000000（取反）
  // c == ctl = 111 000000000000000000111
  // 111 000000000000000000111
  // 111 000000000000000000000
  // 111 000000000000000000000	获取到了运行状态
  private static int runStateOf(int c)     { return c & ~CAPACITY; }
  ```

- 获取当前线程池线程数量：

  ```java
  //        c = 111 000000000000000000111
  // CAPACITY = 000 111111111111111111111
  //            000 000000000000000000111 => 7
  private static int workerCountOf(int c)  { return c & CAPACITY; }
  ```

- 重置当前线程池状态 ctl：

  ```java
  // rs 表示线程池状态，wc 表示当前线程池中 worker（线程）数量，相与以后就是合并后的状态
  private static int ctlOf(int rs, int wc) { return rs | wc; }
  ```

- 比较当前线程池 ctl 所表示的状态：

  ```java
  // 比较当前线程池 ctl 所表示的状态，是否小于某个状态 s
  // 状态对比：RUNNING < SHUTDOWN < STOP < TIDYING < TERMINATED
  private static boolean runStateLessThan(int c, int s) { return c < s; }
  // 比较当前线程池 ctl 所表示的状态，是否大于等于某个状态s
  private static boolean runStateAtLeast(int c, int s) { return c >= s; }
  // 小于 SHUTDOWN 的一定是 RUNNING，SHUTDOWN == 0
  private static boolean isRunning(int c) { return c < SHUTDOWN; }
  ```

- 设置线程池 ctl：

  ```java
  // 使用 CAS 方式 让 ctl 值 +1 ，成功返回 true, 失败返回 false
  private boolean compareAndIncrementWorkerCount(int expect) {
      return ctl.compareAndSet(expect, expect + 1);
  }
  // 使用 CAS 方式 让 ctl 值 -1 ，成功返回 true, 失败返回 false
  private boolean compareAndDecrementWorkerCount(int expect) {
      return ctl.compareAndSet(expect, expect - 1);
  }
  // 将 ctl 值减一，do while 循环会一直重试，直到成功为止
  private void decrementWorkerCount() {
      do {} while (!compareAndDecrementWorkerCount(ctl.get()));
  }
  ```

------

#### 成员属性

成员变量

- **线程池中存放 Worker 的容器**：线程池没有初始化，直接往池中加线程即可

  ```java
  private final HashSet<Worker> workers = new HashSet<Worker>();
  ```

- 线程全局锁：

  ```java
  // 增加减少 worker 或者时修改线程池运行状态需要持有 mainLock
  private final ReentrantLock mainLock = new ReentrantLock();
  ```

- 可重入锁的条件变量：

  ```java
  // 当外部线程调用 awaitTermination() 方法时，会等待当前线程池状态为 Termination 为止
  private final Condition termination = mainLock.newCondition()
  ```

- 线程池相关参数：

  ```java
  private volatile int corePoolSize;				// 核心线程数量
  private volatile int maximumPoolSize;			// 线程池最大线程数量
  private volatile long keepAliveTime;			// 空闲线程存活时间
  private volatile ThreadFactory threadFactory;	// 创建线程时使用的线程工厂，默认是 DefaultThreadFactory
  private final BlockingQueue<Runnable> workQueue;// 【超过核心线程提交任务就放入 阻塞队列】
  ```

  ```java
  private volatile RejectedExecutionHandler handler;	// 拒绝策略，juc包提供了4中方式
  private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();// 默认策略
  ```

- 记录线程池相关属性的数值：

  ```java
  private int largestPoolSize;		// 记录线程池生命周期内线程数最大值
  private long completedTaskCount;	// 记录线程池所完成任务总数，当某个 worker 退出时将完成的任务累加到该属性
  ```

- 控制**核心线程数量内的线程是否可以被回收**：

  ```java
  // false（默认）代表不可以，为 true 时核心线程空闲超过 keepAliveTime 也会被回收
  // allowCoreThreadTimeOut(boolean value) 方法可以设置该值
  private volatile boolean allowCoreThreadTimeOut;
  ```

内部类：

- Worker 类：**每个 Worker 对象会绑定一个初始任务**，启动 Worker 时优先执行，这也是造成线程池不公平的原因。Worker 继承自 AQS，本身具有锁的特性，采用独占锁模式，state = 0 表示未被占用，> 0 表示被占用，< 0 表示初始状态不能被抢锁

  ```java
  private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
  	final Thread thread;			// worker 内部封装的工作线程
      Runnable firstTask;				// worker 第一个执行的任务，普通的 Runnable 实现类或者是 FutureTask
      volatile long completedTasks;	// 记录当前 worker 所完成任务数量
      
      // 构造方法
      Worker(Runnable firstTask) {
          // 设置AQS独占模式为初始化中状态，这个状态不能被抢占锁
         	setState(-1);
          // firstTask不为空时，当worker启动后，内部线程会优先执行firstTask，执行完后会到queue中去获取下个任务
          this.firstTask = firstTask;
          // 使用线程工厂创建一个线程，并且【将当前worker指定为Runnable】，所以thread启动时会调用 worker.run()
          this.thread = getThreadFactory().newThread(this);
      }
      // 【不可重入锁】
      protected boolean tryAcquire(int unused) {
          if (compareAndSetState(0, 1)) {
              setExclusiveOwnerThread(Thread.currentThread());
              return true;
          }
          return false;
      }
  }
  ```

  ```java
  public Thread newThread(Runnable r) {
      // 将当前 worker 指定为 thread 的执行方法，线程调用 start 会调用 r.run()
      Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      if (t.isDaemon())
          t.setDaemon(false);
      if (t.getPriority() != Thread.NORM_PRIORITY)
          t.setPriority(Thread.NORM_PRIORITY);
      return t;
  }
  ```

- 拒绝策略相关的内部类

------

#### 成员方法

##### 提交方法

- AbstractExecutorService#submit()：提交任务，**把 Runnable 或 Callable 任务封装成 FutureTask 执行**，可以通过方法返回的任务对象，调用 get 阻塞获取任务执行的结果或者异常，源码分析在笔记的 Future 部分

  ```java
  public Future<?> submit(Runnable task) {
      // 空指针异常
      if (task == null) throw new NullPointerException();
      // 把 Runnable 封装成未来任务对象，执行结果就是 null，也可以通过参数指定 FutureTask#get 返回数据
      RunnableFuture<Void> ftask = newTaskFor(task, null);
      // 执行方法
      execute(ftask);
      return ftask;
  }
  public <T> Future<T> submit(Callable<T> task) {
      if (task == null) throw new NullPointerException();
      // 把 Callable 封装成未来任务对象
      RunnableFuture<T> ftask = newTaskFor(task);
      // 执行方法
      execute(ftask);	
      // 返回未来任务对象，用来获取返回值
      return ftask;
  }
  ```

  ```java
  protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
      // Runnable 封装成 FutureTask，【指定返回值】
      return new FutureTask<T>(runnable, value);
  }
  protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
      // Callable 直接封装成 FutureTask
      return new FutureTask<T>(callable);
  }
  ```

- execute()：执行任务，**但是没有返回值，没办法获取任务执行结果**，出现异常会直接抛出任务执行时的异常。根据线程池中的线程数，选择添加任务时的处理方式

  ```java
  // command 可以是普通的 Runnable 实现类，也可以是 FutureTask，不能是 Callable
  public void execute(Runnable command) {
      // 非空判断
      if (command == null)
          throw new NullPointerException();
    	// 获取 ctl 最新值赋值给 c，ctl 高 3 位表示线程池状态，低位表示当前线程池线程数量。
      int c = ctl.get();
      // 【1】当前线程数量小于核心线程数，此次提交任务直接创建一个新的 worker，线程池中多了一个新的线程
      if (workerCountOf(c) < corePoolSize) {
          // addWorker 为创建线程的过程，会创建 worker 对象并且将 command 作为 firstTask，优先执行
          if (addWorker(command, true))
              return;
          
          // 执行到这条语句，说明 addWorker 一定是失败的，存在并发现象或者线程池状态被改变，重新获取状态
          // SHUTDOWN 状态下也有可能创建成功，前提 firstTask == null 而且当前 queue 不为空（特殊情况）
          c = ctl.get();
      }
      // 【2】执行到这说明当前线程数量已经达到核心线程数量 或者 addWorker 失败
      // 	判断当前线程池是否处于running状态，成立就尝试将 task 放入到 workQueue 中
      if (isRunning(c) && workQueue.offer(command)) {
          int recheck = ctl.get();
          // 条件一成立说明线程池状态被外部线程给修改了，可能是执行了 shutdown() 方法，该状态不能接收新提交的任务
          // 所以要把刚提交的任务删除，删除成功说明提交之后线程池中的线程还未消费（处理）该任务
          if (!isRunning(recheck) && remove(command))
              // 任务出队成功，走拒绝策略
              reject(command);
          // 执行到这说明线程池是 running 状态，获取线程池中的线程数量，判断是否是 0
          // 【担保机制】，保证线程池在 running 状态下，最起码得有一个线程在工作
          else if (workerCountOf(recheck) == 0)
              addWorker(null, false);
      }
      // 【3】offer失败说明queue满了
      // 如果线程数量尚未达到 maximumPoolSize，会创建非核心 worker 线程直接执行 command，【这也是不公平的原因】
      // 如果当前线程数量达到 maximumPoolSiz，这里 addWorker 也会失败，走拒绝策略
      else if (!addWorker(command, false))
          reject(command);
  }
  ```

------

##### 添加线程

- prestartAllCoreThreads()：**提前预热**，创建所有的核心线程

  ```java
  public int prestartAllCoreThreads() {
      int n = 0;
      while (addWorker(null, true))
          ++n;
      return n;
  }
  ```

- addWorker()：**添加线程到线程池**，返回 true 表示创建 Worker 成功，且线程启动。首先判断线程池是否允许添加线程，允许就让线程数量 + 1，然后去创建 Worker 加入线程池

  注意：SHUTDOWN 状态也能添加线程，但是要求新加的 Woker 没有 firstTask，而且当前 queue 不为空，所以创建一个线程来帮助线程池执行队列中的任务

  ```java
  // core == true 表示采用核心线程数量限制，false 表示采用 maximumPoolSize
  private boolean addWorker(Runnable firstTask, boolean core) {
      // 自旋【判断当前线程池状态是否允许创建线程】，允许就设置线程数量 + 1
      retry:
      for (;;) {
          // 获取 ctl 的值
          int c = ctl.get();
          // 获取当前线程池运行状态
          int rs = runStateOf(c);	
          
          // 判断当前线程池状态【是否允许添加线程】
          
          // 当前线程池是 SHUTDOWN 状态，但是队列里面还有任务尚未处理完，需要处理完 queue 中的任务
          // 【不允许再提交新的 task，所以 firstTask 为空，但是可以继续添加 worker】
          if (rs >= SHUTDOWN && !(rs == SHUTDOWN && firstTask == null && !workQueue.isEmpty()))
              return false;
          for (;;) {
              // 获取线程池中线程数量
              int wc = workerCountOf(c);
              // 条件一一般不成立，CAPACITY是5亿多，根据 core 判断使用哪个大小限制线程数量，超过了返回 false
              if (wc >= CAPACITY || wc >= (core ? corePoolSize : maximumPoolSize))
                  return false;
              // 记录线程数量已经加 1，类比于申请到了一块令牌，条件失败说明其他线程修改了数量
              if (compareAndIncrementWorkerCount(c))
                  // 申请成功，跳出了 retry 这个 for 自旋
                  break retry;
              // CAS 失败，没有成功的申请到令牌
              c = ctl.get();
              // 判断当前线程池状态是否发生过变化，被其他线程修改了，可能其他线程调用了 shutdown() 方法
              if (runStateOf(c) != rs)
                  // 返回外层循环检查是否能创建线程，在 if 语句中返回 false
                  continue retry;
             
          }
      }
      
      //【令牌申请成功，开始创建线程】
      
  	// 运行标记，表示创建的 worker 是否已经启动，false未启动  true启动
      boolean workerStarted = false;
      // 添加标记，表示创建的 worker 是否添加到池子中了，默认false未添加，true是添加。
      boolean workerAdded = false;
      Worker w = null;
      try {
          // 【创建 Worker，底层通过线程工厂 newThread 方法创建执行线程，指定了首先执行的任务】
          w = new Worker(firstTask);
          // 将新创建的 worker 节点中的线程赋值给 t
          final Thread t = w.thread;
          // 这里的判断为了防止 程序员自定义的 ThreadFactory 实现类有 bug，创造不出线程
          if (t != null) {
              final ReentrantLock mainLock = this.mainLock;
              // 加互斥锁，要添加 worker 了
              mainLock.lock();
              try {
                  // 获取最新线程池运行状态保存到 rs
                  int rs = runStateOf(ctl.get());
  				// 判断线程池是否为RUNNING状态，不是再【判断当前是否为SHUTDOWN状态且firstTask为空，特殊情况】
                  if (rs < SHUTDOWN || (rs == SHUTDOWN && firstTask == null)) {
                      // 当线程start后，线程isAlive会返回true，这里还没开始启动线程，如果被启动了就需要报错
                      if (t.isAlive())
                          throw new IllegalThreadStateException();
                      
                      //【将新建的 Worker 添加到线程池中】
                      workers.add(w);
                      int s = workers.size();
  					// 当前池中的线程数量是一个新高，更新 largestPoolSize
                      if (s > largestPoolSize)
                          largestPoolSize = s;
                      // 添加标记置为 true
                      workerAdded = true;
                  }
              } finally {
                  // 解锁啊
                  mainLock.unlock();
              }
              // 添加成功就【启动线程执行任务】
              if (workerAdded) {
                  // Thread 类中持有 Runnable 任务对象，调用的是 Runnable 的 run ，也就是 FutureTask
                  t.start();
                  // 运行标记置为 true
                  workerStarted = true;
              }
          }
      } finally {
          // 如果启动线程失败，做清理工作
          if (! workerStarted)
              addWorkerFailed(w);
      }
      // 返回新创建的线程是否启动
      return workerStarted;
  }
  ```

- addWorkerFailed()：清理任务

  ```java
  private void addWorkerFailed(Worker w) {
      final ReentrantLock mainLock = this.mainLock;
      // 持有线程池全局锁，因为操作的是线程池相关的东西
      mainLock.lock();
      try {
          //条件成立需要将 worker 在 workers 中清理出去。
          if (w != null)
              workers.remove(w);
          // 将线程池计数 -1，相当于归还令牌。
          decrementWorkerCount();
          // 尝试停止线程池
          tryTerminate();
      } finally {
          //释放线程池全局锁。
          mainLock.unlock();
      }
  }
  ```

------

##### 运行方法

- Worker#run：Worker 实现了 Runnable 接口，当线程启动时，会调用 Worker 的 run() 方法

  ```java
  public void run() {
      // ThreadPoolExecutor#runWorker()
      runWorker(this);
  }
  ```

- runWorker()：线程启动就要**执行任务**，会一直 while 循环获取任务并执行

  ```java
  final void runWorker(Worker w) {
      Thread wt = Thread.currentThread();	
      // 获取 worker 的 firstTask
      Runnable task = w.firstTask;
      // 引用置空，【防止复用该线程时重复执行该任务】
      w.firstTask = null;
      // 初始化 worker 时设置 state = -1，表示不允许抢占锁
      // 这里需要设置 state = 0 和 exclusiveOwnerThread = null，开始独占模式抢锁
      w.unlock();
      // true 表示发生异常退出，false 表示正常退出。
      boolean completedAbruptly = true;
      try {
          // firstTask 不是 null 就直接运行，否则去 queue 中获取任务
          // 【getTask 如果是阻塞获取任务，会一直阻塞在take方法，直到获取任务，不会走返回null的逻辑】
          while (task != null || (task = getTask()) != null) {
              // worker 加锁，shutdown 时会判断当前 worker 状态，【根据独占锁状态判断是否空闲】
              w.lock();
              
  			// 说明线程池状态大于 STOP，目前处于 STOP/TIDYING/TERMINATION，此时给线程一个中断信号
              if ((runStateAtLeast(ctl.get(), STOP) ||
                   // 说明线程处于 RUNNING 或者 SHUTDOWN 状态，清除打断标记
                   (Thread.interrupted() && runStateAtLeast(ctl.get(), STOP))) && !wt.isInterrupted())
                  // 中断线程，设置线程的中断标志位为 true
                  wt.interrupt();
              try {
                  // 钩子方法，【任务执行的前置处理】
                  beforeExecute(wt, task);
                  Throwable thrown = null;
                  try {
                      // 【执行任务】
                      task.run();
                  } catch (Exception x) {
                   	//.....
                  } finally {
                      // 钩子方法，【任务执行的后置处理】
                      afterExecute(task, thrown);
                  }
              } finally {
                  task = null;		// 将局部变量task置为null，代表任务执行完成
                  w.completedTasks++;	// 更新worker完成任务数量
                  w.unlock();			// 解锁
              }
          }
          // getTask()方法返回null时会走到这里，表示queue为空并且线程空闲超过保活时间，【当前线程执行退出逻辑】
          completedAbruptly = false;	
      } finally {
          // 正常退出 completedAbruptly = false
         	// 异常退出 completedAbruptly = true，【从 task.run() 内部抛出异常】时，跳到这一行
          processWorkerExit(w, completedAbruptly);
      }
  }
  ```

- unlock()：重置锁

  ```java
  public void unlock() { release(1); }
  // 外部不会直接调用这个方法 这个方法是 AQS 内调用的，外部调用 unlock 时触发此方法
  protected boolean tryRelease(int unused) {
      setExclusiveOwnerThread(null);		// 设置持有者为 null
      setState(0);						// 设置 state = 0
      return true;
  }
  ```

- getTask()：获取任务，线程空闲时间超过 keepAliveTime 就会被回收，判断的依据是**当前线程阻塞获取任务超过保活时间**，方法返回 null 就代表当前线程要被回收了，返回到 runWorker 执行线程退出逻辑。线程池具有担保机制，对于 RUNNING 状态下的超时回收，要保证线程池中最少有一个线程运行，或者任务阻塞队列已经是空

  ```java
  private Runnable getTask() {
      // 超时标记，表示当前线程获取任务是否超时，true 表示已超时
      boolean timedOut = false; 
      for (;;) {
          int c = ctl.get();
          // 获取线程池当前运行状态
          int rs = runStateOf(c);
  		
          // 【tryTerminate】打断线程后执行到这，此时线程池状态为STOP或者线程池状态为SHUTDOWN并且队列已经是空
          // 所以下面的 if 条件一定是成立的，可以直接返回 null，线程就应该退出了
          if (rs >= SHUTDOWN && (rs >= STOP || workQueue.isEmpty())) {
              // 使用 CAS 自旋的方式让 ctl 值 -1
              decrementWorkerCount();
              return null;
          }
          
  		// 获取线程池中的线程数量
          int wc = workerCountOf(c);
  
          // 线程没有明确的区分谁是核心或者非核心线程，是根据当前池中的线程数量判断
          
          // timed = false 表示当前这个线程 获取task时不支持超时机制的，当前线程会使用 queue.take() 阻塞获取
          // timed = true 表示当前这个线程 获取task时支持超时机制，使用 queue.poll(xxx,xxx) 超时获取
          // 条件一代表允许回收核心线程，那就无所谓了，全部线程都执行超时回收
          // 条件二成立说明线程数量大于核心线程数，当前线程认为是非核心线程，有保活时间，去超时获取任务
          boolean timed = allowCoreThreadTimeOut || wc > corePoolSize;
          
  		// 如果线程数量是否超过最大线程数，直接回收
          // 如果当前线程【允许超时回收并且已经超时了】，就应该被回收了，由于【担保机制】还要做判断：
          // 	  wc > 1 说明线程池还用其他线程，当前线程可以直接回收
          //    workQueue.isEmpty() 前置条件是 wc = 1，【如果当前任务队列也是空了，最后一个线程就可以退出】
          if ((wc > maximumPoolSize || (timed && timedOut)) && (wc > 1 || workQueue.isEmpty())) {
              // 使用 CAS 机制将 ctl 值 -1 ,减 1 成功的线程，返回 null，代表可以退出
              if (compareAndDecrementWorkerCount(c))
                  return null;
              continue;
          }
  
          try {
              // 根据当前线程是否需要超时回收，【选择从队列获取任务的方法】是超时获取或者阻塞获取
              Runnable r = timed ?
                  workQueue.poll(keepAliveTime, TimeUnit.NANOSECONDS) : workQueue.take();
              // 获取到任务返回任务，【阻塞获取会阻塞到获取任务为止】，不会返回 null
              if (r != null)
                  return r;
              // 获取任务为 null 说明超时了，将超时标记设置为 true，下次自旋时返 null
              timedOut = true;
          } catch (InterruptedException retry) {
              // 阻塞线程被打断后超时标记置为 false，【说明被打断不算超时】，要继续获取，直到超时或者获取到任务
              // 如果线程池 SHUTDOWN 状态下的打断，会在循环获取任务前判断，返回 null
              timedOut = false;
          }
      }
  }
  ```

- processWorkerExit()：**线程退出线程池**，也有担保机制，保证队列中的任务被执行

  ```java
  // 正常退出 completedAbruptly = false，异常退出为 true
  private void processWorkerExit(Worker w, boolean completedAbruptly) {
      // 条件成立代表当前 worker 是发生异常退出的，task 任务执行过程中向上抛出异常了
      if (completedAbruptly) 
          // 从异常时到这里 ctl 一直没有 -1，需要在这里 -1
          decrementWorkerCount();
  
      final ReentrantLock mainLock = this.mainLock;
      // 加锁
      mainLock.lock();
      try {
          // 将当前 worker 完成的 task 数量，汇总到线程池的 completedTaskCount
          completedTaskCount += w.completedTasks;
  		// 将 worker 从线程池中移除
          workers.remove(w);
      } finally {
          mainLock.unlock();	// 解锁
      }
  	// 尝试停止线程池，唤醒下一个线程
      tryTerminate();
  
      int c = ctl.get();
      // 线程池不是停止状态就应该有线程运行【担保机制】
      if (runStateLessThan(c, STOP)) {
          // 正常退出的逻辑，是对空闲线程回收，不是执行出错
          if (!completedAbruptly) {
              // 根据是否回收核心线程确定【线程池中的线程数量最小值】
              int min = allowCoreThreadTimeOut ? 0 : corePoolSize;
              // 最小值为 0，但是线程队列不为空，需要一个线程来完成任务担保机制
              if (min == 0 && !workQueue.isEmpty())
                  min = 1;
              // 线程池中的线程数量大于最小值可以直接返回
              if (workerCountOf(c) >= min)
                  return;
          }
          // 执行 task 时发生异常，有个线程因为异常终止了，需要添加
          // 或者线程池中的数量小于最小值，这里要创建一个新 worker 加进线程池
          addWorker(null, false);
      }
  }
  ```

------

##### 停止方法

- shutdown()：停止线程池

  ```java
  public void shutdown() {
      final ReentrantLock mainLock = this.mainLock;
      // 获取线程池全局锁
      mainLock.lock();
      try {
          checkShutdownAccess();
          // 设置线程池状态为 SHUTDOWN，如果线程池状态大于 SHUTDOWN，就不会设置直接返回
          advanceRunState(SHUTDOWN);
          // 中断空闲线程
          interruptIdleWorkers();
          // 空方法，子类可以扩展
          onShutdown(); 
      } finally {
          // 释放线程池全局锁
          mainLock.unlock();
      }
      tryTerminate();
  }
  ```

- interruptIdleWorkers()：shutdown 方法会**中断所有空闲线程**，根据是否可以获取 AQS 独占锁判断是否处于工作状态。线程之所以空闲是因为阻塞队列没有任务，不会中断正在运行的线程，所以 shutdown 方法会让所有的任务执行完毕

  ```java
  // onlyOne == true 说明只中断一个线程 ，false 则中断所有线程
  private void interruptIdleWorkers(boolean onlyOne) {
      final ReentrantLock mainLock = this.mainLock;
      / /持有全局锁
      mainLock.lock();
      try {
          // 遍历所有 worker
          for (Worker w : workers) {
              // 获取当前 worker 的线程
              Thread t = w.thread;
              // 条件一成立：说明当前迭代的这个线程尚未中断
              // 条件二成立：说明【当前worker处于空闲状态】，阻塞在poll或者take，因为worker执行task时是要加锁的
              //           每个worker有一个独占锁，w.tryLock()尝试加锁，加锁成功返回 true
              if (!t.isInterrupted() && w.tryLock()) {
                  try {
                      // 中断线程，处于 queue 阻塞的线程会被唤醒，进入下一次自旋，返回 null，执行退出相逻辑
                      t.interrupt();
                  } catch (SecurityException ignore) {
                  } finally {
                      // 释放worker的独占锁
                      w.unlock();
                  }
              }
              // false，代表中断所有的线程
              if (onlyOne)
                  break;
          }
  
      } finally {
          // 释放全局锁
          mainLock.unlock();
      }
  }
  ```

- shutdownNow()：直接关闭线程池，不会等待任务执行完成

  ```java
  public List<Runnable> shutdownNow() {
      // 返回值引用
      List<Runnable> tasks;
      final ReentrantLock mainLock = this.mainLock;
      // 获取线程池全局锁
      mainLock.lock();
      try {
          checkShutdownAccess();
          // 设置线程池状态为STOP
          advanceRunState(STOP);
          // 中断线程池中【所有线程】
          interruptWorkers();
          // 从阻塞队列中导出未处理的task
          tasks = drainQueue();
      } finally {
          mainLock.unlock();
      }
  
      tryTerminate();
      // 返回当前任务队列中 未处理的任务。
      return tasks;
  }
  ```

- tryTerminate()：设置为 TERMINATED 状态 if either (SHUTDOWN and pool and queue empty) or (STOP and pool empty)

  ```java
  final void tryTerminate() {
      for (;;) {
          // 获取 ctl 的值
          int c = ctl.get();
          // 线程池正常，或者有其他线程执行了状态转换的方法，当前线程直接返回
          if (isRunning(c) || runStateAtLeast(c, TIDYING) ||
              // 线程池是 SHUTDOWN 并且任务队列不是空，需要去处理队列中的任务
              (runStateOf(c) == SHUTDOWN && ! workQueue.isEmpty()))
              return;
          
          // 执行到这里说明线程池状态为 STOP 或者线程池状态为 SHUTDOWN 并且队列已经是空
          // 判断线程池中线程的数量
          if (workerCountOf(c) != 0) {
              // 【中断一个空闲线程】，在 queue.take() | queue.poll() 阻塞空闲
              // 唤醒后的线程会在getTask()方法返回null，
              // 执行 processWorkerExit 退出逻辑时会再次调用 tryTerminate() 唤醒下一个空闲线程
              interruptIdleWorkers(ONLY_ONE);
              return;
          }
  		// 池中的线程数量为 0 来到这里
          final ReentrantLock mainLock = this.mainLock;
          // 加全局锁
          mainLock.lock();
          try {
              // 设置线程池状态为 TIDYING 状态，线程数量为 0
              if (ctl.compareAndSet(c, ctlOf(TIDYING, 0))) {
                  try {
                      // 结束线程池
                      terminated();
                  } finally {
                      // 设置线程池状态为TERMINATED状态。
                      ctl.set(ctlOf(TERMINATED, 0));
                      // 【唤醒所有调用 awaitTermination() 方法的线程】
                      termination.signalAll();
                  }
                  return;
              }
          } finally {
  			// 释放线程池全局锁
              mainLock.unlock();
          }
      }
  }
  ```

------

#### Future

##### 线程使用

FutureTask 未来任务对象，继承 Runnable、Future 接口，用于包装 Callable 对象，实现任务的提交

```java
public static void main(String[] args) throws ExecutionException, InterruptedException {
    FutureTask<String> task = new FutureTask<>(new Callable<String>() {
        @Override
        public String call() throws Exception {
            return "Hello World";
        }
    });
    new Thread(task).start();	//启动线程
    String msg = task.get();	//获取返回任务数据
    System.out.println(msg);
}
```

构造方法：

```java
public FutureTask(Callable<V> callable){
	this.callable = callable;	// 属性注入
    this.state = NEW; 			// 任务状态设置为 new
}
public FutureTask(Runnable runnable, V result) {
    // 适配器模式
    this.callable = Executors.callable(runnable, result);
    this.state = NEW;       
}
public static <T> Callable<T> callable(Runnable task, T result) {
    if (task == null) throw new NullPointerException();
    // 使用装饰者模式将 runnable 转换成 callable 接口，外部线程通过 get 获取
    // 当前任务执行结果时，结果可能为 null 也可能为传进来的值，【传进来什么返回什么】
    return new RunnableAdapter<T>(task, result);
}
static final class RunnableAdapter<T> implements Callable<T> {
    final Runnable task;
    final T result;
    // 构造方法
    RunnableAdapter(Runnable task, T result) {
        this.task = task;
        this.result = result;
    }
    public T call() {
        // 实则调用 Runnable#run 方法
        task.run();
        // 返回值为构造 FutureTask 对象时传入的返回值或者是 null
        return result;
    }
}
```

------

##### 成员属性

FutureTask 类的成员属性：

- 任务状态：

  ```java
  // 表示当前task状态
  private volatile int state;
  // 当前任务尚未执行
  private static final int NEW          = 0;
  // 当前任务正在结束，尚未完全结束，一种临界状态
  private static final int COMPLETING   = 1;
  // 当前任务正常结束
  private static final int NORMAL       = 2;
  // 当前任务执行过程中发生了异常，内部封装的 callable.run() 向上抛出异常了
  private static final int EXCEPTIONAL  = 3;
  // 当前任务被取消
  private static final int CANCELLED    = 4;
  // 当前任务中断中
  private static final int INTERRUPTING = 5;
  // 当前任务已中断
  private static final int INTERRUPTED  = 6;
  ```

- 任务对象：

  ```java
  private Callable<V> callable;	// Runnable 使用装饰者模式伪装成 Callable
  ```

- **存储任务执行的结果**，这是 run 方法返回值是 void 也可以获取到执行结果的原因：

  ```java
  // 正常情况下：任务正常执行结束，outcome 保存执行结果，callable 返回值
  // 非正常情况：callable 向上抛出异常，outcome 保存异常
  private Object outcome; 
  ```

- 执行当前任务的线程对象：

  ```java
  private volatile Thread runner;	// 当前任务被线程执行期间，保存当前执行任务的线程对象引用
  ```

- **线程阻塞队列的头节点**：

  ```java
  // 会有很多线程去 get 当前任务的结果，这里使用了一种数据结构头插头取（类似栈）的一个队列来保存所有的 get 线程
  private volatile WaitNode waiters;
  ```

- 内部类：

  ```java
  static final class WaitNode {
      // 单向链表
      volatile Thread thread;
      volatile WaitNode next;
      WaitNode() { thread = Thread.currentThread(); }
  }
  ```

------

##### 成员方法

FutureTask 类的成员方法：

- **FutureTask#run**：任务执行入口

  ```java
  public void run() {
      //条件一：成立说明当前 task 已经被执行过了或者被 cancel 了，非 NEW 状态的任务，线程就不需要处理了
      //条件二：线程是 NEW 状态，尝试设置当前任务对象的线程是当前线程，设置失败说明其他线程抢占了该任务，直接返回
      if (state != NEW ||
          !UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))
          return;
      try {
          // 执行到这里，当前 task 一定是 NEW 状态，而且【当前线程也抢占 task 成功】
          Callable<V> c = callable;
          // 判断任务是否为空，防止空指针异常；判断 state 状态，防止外部线程在此期间 cancel 掉当前任务
          // 【因为 task 的执行者已经设置为当前线程，所以这里是线程安全的】
          if (c != null && state == NEW) {
              V result;
              // true 表示 callable.run 代码块执行成功 未抛出异常
              // false 表示 callable.run 代码块执行失败 抛出异常
              boolean ran;
              try {
  				// 【调用自定义的方法，执行结果赋值给 result】
                  result = c.call();
                  // 没有出现异常
                  ran = true;
              } catch (Throwable ex) {
                  // 出现异常，返回值置空，ran 置为 false
                  result = null;
                  ran = false;
                  // 设置返回的异常
                  setException(ex);
              }
              // 代码块执行正常
              if (ran)
                  // 设置返回的结果
                  set(result);
          }
      } finally {
          // 任务执行完成，取消线程的引用，help GC
          runner = null;
          int s = state;
          // 判断任务是不是被中断
          if (s >= INTERRUPTING)
              // 执行中断处理方法
              handlePossibleCancellationInterrupt(s);
      }
  }
  ```

  FutureTask#set：设置正常返回值，首先将任务状态设置为 COMPLETING 状态代表完成中，逻辑执行完设置为 NORMAL 状态代表任务正常执行完成，最后唤醒 get() 阻塞线程

  ```java
  protected void set(V v) {
      // CAS 方式设置当前任务状态为完成中，设置失败说明其他线程取消了该任务
      if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
          // 【将结果赋值给 outcome】
          outcome = v;
          // 将当前任务状态修改为 NORMAL 正常结束状态。
          UNSAFE.putOrderedInt(this, stateOffset, NORMAL);
          finishCompletion();
      }
  }
  ```

  FutureTask#setException：设置异常返回值

  ```java
  protected void setException(Throwable t) {
      if (UNSAFE.compareAndSwapInt(this, stateOffset, NEW, COMPLETING)) {
          // 赋值给返回结果，用来向上层抛出来的异常
          outcome = t;
          // 将当前任务的状态 修改为 EXCEPTIONAL
          UNSAFE.putOrderedInt(this, stateOffset, EXCEPTIONAL);
          finishCompletion();
      }
  }
  ```

  FutureTask#finishCompletion：**唤醒 get() 阻塞线程**

  ```java
  private void finishCompletion() {
      // 遍历所有的等待的节点，q 指向头节点
      for (WaitNode q; (q = waiters) != null;) {
          // 使用cas设置 waiters 为 null，防止外部线程使用cancel取消当前任务，触发finishCompletion方法重复执行
          if (UNSAFE.compareAndSwapObject(this, waitersOffset, q, null)) {
              // 自旋
              for (;;) {
                  // 获取当前 WaitNode 节点封装的 thread
                  Thread t = q.thread;
                  // 当前线程不为 null，唤醒当前 get() 等待获取数据的线程
                  if (t != null) {
                      q.thread = null;
                      LockSupport.unpark(t);
                  }
                  // 获取当前节点的下一个节点
                  WaitNode next = q.next;
                  // 当前节点是最后一个节点了
                  if (next == null)
                      break;
                  // 断开链表
                  q.next = null; // help gc
                  q = next;
              }
              break;
          }
      }
      done();
      callable = null;	// help GC
  }
  ```

  FutureTask#handlePossibleCancellationInterrupt：任务中断处理

  ```java
  private void handlePossibleCancellationInterrupt(int s) {
      if (s == INTERRUPTING)
          // 中断状态中
          while (state == INTERRUPTING)
              // 等待中断完成
              Thread.yield();
  }
  ```

- **FutureTask#get**：获取任务执行的返回值，执行 run 和 get 的不是同一个线程，一般有多个线程 get，只有一个线程 run

  ```java
  public V get() throws InterruptedException, ExecutionException {
      // 获取当前任务状态
      int s = state;
      // 条件成立说明任务还没执行完成
      if (s <= COMPLETING)
          // 返回 task 当前状态，可能当前线程在里面已经睡了一会
          s = awaitDone(false, 0L);
      return report(s);
  }
  ```

  FutureTask#awaitDone：**get 线程封装成 WaitNode 对象进入阻塞队列阻塞等待**

  ```java
  private int awaitDone(boolean timed, long nanos) throws InterruptedException {
      // 0 不带超时
      final long deadline = timed ? System.nanoTime() + nanos : 0L;
      // 引用当前线程，封装成 WaitNode 对象
      WaitNode q = null;
      // 表示当前线程 waitNode 对象，是否进入阻塞队列
      boolean queued = false;
      // 【三次自旋开始休眠】
      for (;;) {
          // 判断当前 get() 线程是否被打断，打断返回 true，清除打断标记
          if (Thread.interrupted()) {
              // 当前线程对应的等待 node 出队，
              removeWaiter(q);
              throw new InterruptedException();
          }
  		// 获取任务状态
          int s = state;
          // 条件成立说明当前任务执行完成已经有结果了
          if (s > COMPLETING) {
              // 条件成立说明已经为当前线程创建了 WaitNode，置空 help GC
              if (q != null)
                  q.thread = null;
              // 返回当前的状态
              return s;
          }
          // 条件成立说明当前任务接近完成状态，这里让当前线程释放一下 cpu ，等待进行下一次抢占 cpu
          else if (s == COMPLETING) 
              Thread.yield();
          // 【第一次自旋】，当前线程还未创建 WaitNode 对象，此时为当前线程创建 WaitNode对象
          else if (q == null)
              q = new WaitNode();
          // 【第二次自旋】，当前线程已经创建 WaitNode 对象了，但是node对象还未入队
          else if (!queued)
              // waiters 指向队首，让当前 WaitNode 成为新的队首，【头插法】，失败说明其他线程修改了新的队首
              queued = UNSAFE.compareAndSwapObject(this, waitersOffset, q.next = waiters, q);
          // 【第三次自旋】，会到这里，或者 else 内
          else if (timed) {
              nanos = deadline - System.nanoTime();
              if (nanos <= 0L) {
                  removeWaiter(q);
                  return state;
              }
              // 阻塞指定的时间
              LockSupport.parkNanos(this, nanos);
          }
          // 条件成立：说明需要阻塞
          else
              // 【当前 get 操作的线程被 park 阻塞】，除非有其它线程将唤醒或者将当前线程中断
              LockSupport.park(this);
      }
  }
  ```

  FutureTask#report：封装运行结果，可以获取 run() 方法中设置的成员变量 outcome，**这是 run 方法的返回值是 void 也可以获取到任务执行的结果的原因**

  ```java
  private V report(int s) throws ExecutionException {
      // 获取执行结果，是在一个 futuretask 对象中的属性，可以直接获取
      Object x = outcome;
      // 当前任务状态正常结束
      if (s == NORMAL)
          return (V)x;	// 直接返回 callable 的逻辑结果
      // 当前任务被取消或者中断
      if (s >= CANCELLED)
          throw new CancellationException();		// 抛出异常
      // 执行到这里说明自定义的 callable 中的方法有异常，使用 outcome 上层抛出异常
      throw new ExecutionException((Throwable)x);	
  }
  ```

- FutureTask#cancel：任务取消，打断正在执行该任务的线程

  ```java
  public boolean cancel(boolean mayInterruptIfRunning) {
      // 条件一：表示当前任务处于运行中或者处于线程池任务队列中
      // 条件二：表示修改状态，成功可以去执行下面逻辑，否则返回 false 表示 cancel 失败
      if (!(state == NEW &&
            UNSAFE.compareAndSwapInt(this, stateOffset, NEW,
                                     mayInterruptIfRunning ? INTERRUPTING : CANCELLED)))
          return false;
      try {
          // 如果任务已经被执行，是否允许打断
          if (mayInterruptIfRunning) {
              try {
                  // 获取执行当前 FutureTask 的线程
                  Thread t = runner;
                  if (t != null)
                      // 打断执行的线程
                      t.interrupt();
              } finally {
                  // 设置任务状态为【中断完成】
                  UNSAFE.putOrderedInt(this, stateOffset, INTERRUPTED);
              }
          }
      } finally {
          // 唤醒所有 get() 阻塞的线程
          finishCompletion();
      }
      return true;
  }
  ```

------

### 任务调度

#### Timer

Timer 实现定时功能，Timer 的优点在于简单易用，但由于所有任务都是由同一个线程来调度，因此所有任务都是串行执行的，同一时间只能有一个任务在执行，前一个任务的延迟或异常都将会影响到之后的任务

```java
private static void method1() {
    Timer timer = new Timer();
    TimerTask task1 = new TimerTask() {
        @Override
        public void run() {
            System.out.println("task 1");
            //int i = 1 / 0;//任务一的出错会导致任务二无法执行
            Thread.sleep(2000);
        }
    };
    TimerTask task2 = new TimerTask() {
        @Override
        public void run() {
            System.out.println("task 2");
        }
    };
    // 使用 timer 添加两个任务，希望它们都在 1s 后执行
	// 但由于 timer 内只有一个线程来顺序执行队列中的任务，因此任务1的延时，影响了任务2的执行
    timer.schedule(task1, 1000);//17:45:56 c.ThreadPool [Timer-0] - task 1
    timer.schedule(task2, 1000);//17:45:58 c.ThreadPool [Timer-0] - task 2
}
```

------

#### Scheduled

任务调度线程池 ScheduledThreadPoolExecutor 继承 ThreadPoolExecutor：

- 使用内部类 ScheduledFutureTask 封装任务
- 使用内部类 DelayedWorkQueue 作为线程池队列
- 重写 onShutdown 方法去处理 shutdown 后的任务
- 提供 decorateTask 方法作为 ScheduledFutureTask 的修饰方法，以便开发者进行扩展

构造方法：`Executors.newScheduledThreadPool(int corePoolSize)`

```java
public ScheduledThreadPoolExecutor(int corePoolSize) {
    // 最大线程数固定为 Integer.MAX_VALUE，保活时间 keepAliveTime 固定为 0
    super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
          // 阻塞队列是 DelayedWorkQueue
          new DelayedWorkQueue());
}
```

常用 API：

- `ScheduledFuture<?> schedule(Runnable/Callable<V>, long delay, TimeUnit u)`：延迟执行任务
- `ScheduledFuture<?> scheduleAtFixedRate(Runnable/Callable<V>, long initialDelay, long period, TimeUnit unit)`：定时执行周期任务，不考虑执行的耗时，参数为初始延迟时间、间隔时间、单位
- `ScheduledFuture<?> scheduleWithFixedDelay(Runnable/Callable<V>, long initialDelay, long delay, TimeUnit unit)`：定时执行周期任务，考虑执行的耗时，参数为初始延迟时间、间隔时间、单位

基本使用：

- 延迟任务，但是出现异常并不会在控制台打印，也不会影响其他线程的执行

  ```java
  public static void main(String[] args){
      // 线程池大小为1时也是串行执行
      ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
      // 添加两个任务，都在 1s 后同时执行
      executor.schedule(() -> {
      	System.out.println("任务1，执行时间：" + new Date());
          //int i = 1 / 0;
      	try { Thread.sleep(2000); } catch (InterruptedException e) { }
      }, 1000, TimeUnit.MILLISECONDS);
      
      executor.schedule(() -> {
      	System.out.println("任务2，执行时间：" + new Date());
      }, 1000, TimeUnit.MILLISECONDS);
  }
  ```

- 定时任务 scheduleAtFixedRate：**一次任务的启动到下一次任务的启动**之间只要大于等于间隔时间，抢占到 CPU 就会立即执行

  ```
  public static void main(String[] args) {
      ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
      System.out.println("start..." + new Date());
      
      pool.scheduleAtFixedRate(() -> {
          System.out.println("running..." + new Date());
          Thread.sleep(2000);
      }, 1, 1, TimeUnit.SECONDS);
  }
  
  /*start...Sat Apr 24 18:08:12 CST 2021
  running...Sat Apr 24 18:08:13 CST 2021
  running...Sat Apr 24 18:08:15 CST 2021
  running...Sat Apr 24 18:08:17 CST 2021
  ```

- 定时任务 scheduleWithFixedDelay：**一次任务的结束到下一次任务的启动之间**等于间隔时间，抢占到 CPU 就会立即执行，这个方法才是真正的设置两个任务之间的间隔

  ```java
  public static void main(String[] args){
      ScheduledExecutorService pool = Executors.newScheduledThreadPool(3);
      System.out.println("start..." + new Date());
      
      pool.scheduleWithFixedDelay(() -> {
          System.out.println("running..." + new Date());
          Thread.sleep(2000);
      }, 1, 1, TimeUnit.SECONDS);
  }
  /*start...Sat Apr 24 18:11:41 CST 2021
  running...Sat Apr 24 18:11:42 CST 2021
  running...Sat Apr 24 18:11:45 CST 2021
  running...Sat Apr 24 18:11:48 CST 2021
  ```

------

#### 成员属性

##### 成员变量

- shutdown 后是否继续执行周期任务：

  ```java
  private volatile boolean continueExistingPeriodicTasksAfterShutdown;
  ```

- shutdown 后是否继续执行延迟任务：

  ```java
  private volatile boolean executeExistingDelayedTasksAfterShutdown = true;
  ```

- 取消方法是否将该任务从队列中移除：

  ```java
  // 默认 false，不移除，等到线程拿到任务之后抛弃
  private volatile boolean removeOnCancel = false;
  ```

- 任务的序列号，可以用来比较优先级：

  ```java
  private static final AtomicLong sequencer = new AtomicLong();
  ```

------

##### 延迟任务

ScheduledFutureTask 继承 FutureTask，实现 RunnableScheduledFuture 接口，具有延迟执行的特点，覆盖 FutureTask 的 run 方法来实现对**延时执行、周期执行**的支持。对于延时任务调用 FutureTask#run，而对于周期性任务则调用 FutureTask#runAndReset 并且在成功之后根据 fixed-delay/fixed-rate 模式来设置下次执行时间并重新将任务塞到工作队列

在调度线程池中无论是 runnable 还是 callable，无论是否需要延迟和定时，所有的任务都会被封装成 ScheduledFutureTask

成员变量：

- 任务序列号：

  ```java
  private final long sequenceNumber;
  ```

- 执行时间：

  ```java
  private long time;			// 任务可以被执行的时间，交付时间，以纳秒表示
  private final long period;	// 0 表示非周期任务，正数表示 fixed-rate 模式的周期，负数表示 fixed-delay 模式
  ```

  fixed-rate：两次开始启动的间隔，fixed-delay：一次执行结束到下一次开始启动

- 实际的任务对象：

  ```java
  RunnableScheduledFuture<V> outerTask = this;
  ```

- 任务在队列数组中的索引下标：

  ```java
  // DelayedWorkQueue 底层使用的数据结构是最小堆，记录当前任务在堆中的索引，-1 代表删除
  int heapIndex;
  ```

成员方法：

- 构造方法：

  ```java
  ScheduledFutureTask(Runnable r, V result, long ns, long period) {
      super(r, result);
      // 任务的触发时间
      this.time = ns;
      // 任务的周期，多长时间执行一次
      this.period = period;
      // 任务的序号
      this.sequenceNumber = sequencer.getAndIncrement();
  }
  ```

- compareTo()：ScheduledFutureTask 根据执行时间 time 正序排列，如果执行时间相同，在按照序列号 sequenceNumber 正序排列，任务需要放入 DelayedWorkQueue，延迟队列中使用该方法按照从小到大进行排序

  ```java
  public int compareTo(Delayed other) {
      if (other == this) // compare zero if same object
          return 0;
      if (other instanceof ScheduledFutureTask) {
          // 类型强转
          ScheduledFutureTask<?> x = (ScheduledFutureTask<?>)other;
          // 比较者 - 被比较者的执行时间
          long diff = time - x.time;
          // 比较者先执行
          if (diff < 0)
              return -1;
          // 被比较者先执行
          else if (diff > 0)
              return 1;
          // 比较者的序列号小
          else if (sequenceNumber < x.sequenceNumber)
              return -1;
          else
              return 1;
      }
      // 不是 ScheduledFutureTask 类型时，根据延迟时间排序
      long diff = getDelay(NANOSECONDS) - other.getDelay(NANOSECONDS);
      return (diff < 0) ? -1 : (diff > 0) ? 1 : 0;
  }
  ```

- run()：执行任务，非周期任务直接完成直接结束，**周期任务执行完后会设置下一次的执行时间，重新放入线程池的阻塞队列**，如果线程池中的线程数量少于核心线程，就会添加 Worker 开启新线程

  ```java
  public void run() {
      // 是否周期性，就是判断 period 是否为 0
      boolean periodic = isPeriodic();
      // 根据是否是周期任务检查当前状态能否执行任务，不能执行就取消任务
      if (!canRunInCurrentRunState(periodic))
          cancel(false);
      // 非周期任务，直接调用 FutureTask#run 执行
      else if (!periodic)
          ScheduledFutureTask.super.run();
      // 周期任务的执行，返回 true 表示执行成功
      else if (ScheduledFutureTask.super.runAndReset()) {
          // 设置周期任务的下一次执行时间
          setNextRunTime();
          // 任务的下一次执行安排，如果当前线程池状态可以执行周期任务，加入队列，并开启新线程
          reExecutePeriodic(outerTask);
      }
  }
  ```

  周期任务正常完成后**任务的状态不会变化**，依旧是 NEW，不会设置 outcome 属性。但是如果本次任务执行出现异常，会进入 setException 方法将任务状态置为异常，把异常保存在 outcome 中，方法返回 false，后续的该任务将不会再周期的执行

  ```java
  protected boolean runAndReset() {
      // 任务不是新建的状态了，或者被别的线程执行了，直接返回 false
      if (state != NEW ||
          !UNSAFE.compareAndSwapObject(this, runnerOffset, null, Thread.currentThread()))
          return false;
      boolean ran = false;
      int s = state;
      try {
          Callable<V> c = callable;
          if (c != null && s == NEW) {
              try {
                  // 执行方法，没有返回值
                  c.call();
                  ran = true;
              } catch (Throwable ex) {
                  // 出现异常，把任务设置为异常状态，唤醒所有的 get 阻塞线程
                  setException(ex);
              }
          }
      } finally {
  		// 执行完成把执行线程引用置为 null
          runner = null;
          s = state;
          // 如果线程被中断进行中断处理
          if (s >= INTERRUPTING)
              handlePossibleCancellationInterrupt(s);
      }
      // 如果正常执行，返回 true，并且任务状态没有被取消
      return ran && s == NEW;
  }
  ```

  ```java
  // 任务下一次的触发时间
  private void setNextRunTime() {
      long p = period;
      if (p > 0)
          // fixed-rate 模式，【时间设置为上一次执行任务的时间 + p】，两次任务执行的时间差
          time += p;
      else
          // fixed-delay 模式，下一次执行时间是【当前这次任务结束的时间（就是现在） + delay 值】
          time = triggerTime(-p);
  }
  ```

- reExecutePeriodic()**：准备任务的下一次执行，重新放入阻塞任务队列**

  ```java
  // ScheduledThreadPoolExecutor#reExecutePeriodic
  void reExecutePeriodic(RunnableScheduledFuture<?> task) {
      if (canRunInCurrentRunState(true)) {
          // 【放入任务队列】
          super.getQueue().add(task);
          // 如果提交完任务之后，线程池状态变为了 shutdown 状态，需要再次检查是否可以执行，
          // 如果不能执行且任务还在队列中未被取走，则取消任务
          if (!canRunInCurrentRunState(true) && remove(task))
              task.cancel(false);
          else
              // 当前线程池状态可以执行周期任务，加入队列，并【根据线程数量是否大于核心线程数确定是否开启新线程】
              ensurePrestart();
      }
  }
  ```

- cancel()：取消任务

  ```java
  public boolean cancel(boolean mayInterruptIfRunning) {
      // 调用父类 FutureTask#cancel 来取消任务
      boolean cancelled = super.cancel(mayInterruptIfRunning);
      // removeOnCancel 用于控制任务取消后是否应该从阻塞队列中移除
      if (cancelled && removeOnCancel && heapIndex >= 0)
          // 从等待队列中删除该任务，并调用 tryTerminate() 判断是否需要停止线程池
          remove(this);
      return cancelled;
  }
  ```

------

##### 延迟队列

DelayedWorkQueue 是支持延时获取元素的阻塞队列，内部采用优先队列 PriorityQueue（小根堆、满二叉树）存储元素

其他阻塞队列存储节点的数据结构大都是链表，**延迟队列是数组**，所以延迟队列出队头元素后需要**让其他元素（尾）替换到头节点**，防止空指针异常

成员变量：

- 容量：

  ```java
  private static final int INITIAL_CAPACITY = 16;			// 初始容量
  private int size = 0;									// 节点数量
  private RunnableScheduledFuture<?>[] queue = 
      new RunnableScheduledFuture<?>[INITIAL_CAPACITY];	// 存放节点
  ```

- 锁：

  ```java
  private final ReentrantLock lock = new ReentrantLock();	// 控制并发
  private final Condition available = lock.newCondition();// 条件队列
  ```

- 阻塞等待头节点的线程：线程池内的某个线程去 take() 获取任务时，如果延迟队列顶层节点不为 null（队列内有任务），但是节点任务还不到触发时间，线程就去检查**队列的 leader字段**是否被占用

  - 如果未被占用，则当前线程占用该字段，然后当前线程到 available 条件队列指定超时时间 `堆顶任务.time - now()` 挂起
  - 如果被占用，当前线程直接到 available 条件队列不指定超时时间的挂起

  ```java
  // leader 在 available 条件队列内是首元素，它超时之后会醒过来，然后再次将堆顶元素获取走，获取走之后，take()结束之前，会调用是 available.signal() 唤醒下一个条件队列内的等待者，然后释放 lock，下一个等待者被唤醒后去到 AQS 队列，做 acquireQueue(node) 逻辑
  private Thread leader = null;
  ```

成员方法

- offer()：插入节点

  ```java
  public boolean offer(Runnable x) {
      // 判空
      if (x == null)
          throw new NullPointerException();
      RunnableScheduledFuture<?> e = (RunnableScheduledFuture<?>)x;
      // 队列锁，增加删除数据时都要加锁
      final ReentrantLock lock = this.lock;
      lock.lock();
      try {
          int i = size;
          // 队列数量大于存放节点的数组长度，需要扩容
          if (i >= queue.length)
              // 扩容为原来长度的 1.5 倍
              grow();
          size = i + 1;
          // 当前是第一个要插入的节点
          if (i == 0) {
              queue[0] = e;
              // 修改 ScheduledFutureTask 的 heapIndex 属性，表示该对象在队列里的下标
              setIndex(e, 0);
          } else {
              // 向上调整元素的位置，并更新 heapIndex 
              siftUp(i, e);
          }
          // 情况1：当前任务是第一个加入到 queue 内的任务，所以在当前任务加入到 queue 之前，take() 线程会直接
          //		到 available 队列不设置超时的挂起，并不会去占用 leader 字段，这时需会唤醒一个线程 让它去消费
         	// 情况2：当前任务【优先级最高】，原堆顶任务可能还未到触发时间，leader 线程设置超时的在 available 挂起
          //		原先的 leader 等待的是原先的头节点，所以 leader 已经无效，需要将 leader 线程唤醒，
          //		唤醒之后它会检查堆顶，如果堆顶任务可以被消费，则直接获取走，否则继续成为 leader 等待新堆顶任务
          if (queue[0] == e) {
              // 将 leader 设置为 null
              leader = null;
              // 直接随便唤醒等待头结点的阻塞线程
              available.signal();
          }
      } finally {
          lock.unlock();
      }
      return true;
  }
  ```

  ```java
  // 插入新节点后对堆进行调整，进行节点上移，保持其特性【节点的值小于子节点的值】，小顶堆
  private void siftUp(int k, RunnableScheduledFuture<?> key) {
      while (k > 0) {
          // 父节点，就是堆排序
          int parent = (k - 1) >>> 1;
          RunnableScheduledFuture<?> e = queue[parent];
          // key 和父节点比，如果大于父节点可以直接返回，否则就继续上浮
          if (key.compareTo(e) >= 0)
              break;
          queue[k] = e;
          setIndex(e, k);
          k = parent;
      }
      queue[k] = key;
      setIndex(key, k);
  }
  ```

- poll()：非阻塞获取头结点，**获取执行时间最近并且可以执行的**

  ```java
  // 非阻塞获取
  public RunnableScheduledFuture<?> poll() {
      final ReentrantLock lock = this.lock;
      lock.lock();
      try {
          // 获取队头节点，因为是小顶堆
          RunnableScheduledFuture<?> first = queue[0];
          // 头结点为空或者的延迟时间没到返回 null
          if (first == null || first.getDelay(NANOSECONDS) > 0)
              return null;
          else
              // 头结点达到延迟时间，【尾节点成为替代节点下移调整堆结构】，返回头结点
              return finishPoll(first);
      } finally {
          lock.unlock();
      }
  }
  ```

  ```java
  private RunnableScheduledFuture<?> finishPoll(RunnableScheduledFuture<?> f) {
      // 获取尾索引
      int s = --size;
      // 获取尾节点
      RunnableScheduledFuture<?> x = queue[s];
      // 将堆结构最后一个节点占用的 slot 设置为 null，因为该节点要尝试升级成堆顶，会根据特性下调
      queue[s] = null;
      // s == 0 说明 当前堆结构只有堆顶一个节点，此时不需要做任何的事情
      if (s != 0)
          // 从索引处 0 开始向下调整
          siftDown(0, x);
      // 出队的元素索引设置为 -1
      setIndex(f, -1);
      return f;
  }
  ```

- take()：阻塞获取头节点，读取当前堆中最小的也就是触发时间最近的任务

  ```java
  public RunnableScheduledFuture<?> take() throws InterruptedException {
      final ReentrantLock lock = this.lock;
      // 保证线程安全
      lock.lockInterruptibly();
      try {
          for (;;) {
              // 头节点
              RunnableScheduledFuture<?> first = queue[0];
              if (first == null)
                  // 等待队列不空，直至有任务通过 offer 入队并唤醒
                  available.await();
              else {
                  // 获取头节点的延迟时间是否到时
                  long delay = first.getDelay(NANOSECONDS);
                  if (delay <= 0)
                      // 到达触发时间，获取头节点并调整堆，重新选择延迟时间最小的节点放入头部
                      return finishPoll(first);
                  
                  // 逻辑到这说明头节点的延迟时间还没到
                  first = null;
                  // 说明有 leader 线程在等待获取头节点，当前线程直接去阻塞等待
                  if (leader != null)
                      available.await();
                  else {
                      // 没有 leader 线程，【当前线程作为leader线程，并设置头结点的延迟时间作为阻塞时间】
                      Thread thisThread = Thread.currentThread();
                      leader = thisThread;
                      try {
                          // 在条件队列 available 使用带超时的挂起（堆顶任务.time - now() 纳秒值..）
                          available.awaitNanos(delay);
                          // 到达阻塞时间时，当前线程会从这里醒来来
                      } finally {
                          // t堆顶更新，leader 置为 null，offer 方法释放锁后，
                          // 有其它线程通过 take/poll 拿到锁,读到 leader == null，然后将自身更新为leader。
                          if (leader == thisThread)
                              // leader 置为 null 用以接下来判断是否需要唤醒后继线程
                              leader = null;
                      }
                  }
              }
          }
      } finally {
          // 没有 leader 线程，头结点不为 null，唤醒阻塞获取头节点的线程，
          // 【如果没有这一步，就会出现有了需要执行的任务，但是没有线程去执行】
          if (leader == null && queue[0] != null)
              available.signal();
          lock.unlock();
      }
  }
  ```

- remove()：删除节点，堆移除一个元素的时间复杂度是 O(log n)，**延迟任务维护了 heapIndex**，直接访问的时间复杂度是 O(1)，从而可以更快的移除元素，任务在队列中被取消后会进入该逻辑

  ```java
  public boolean remove(Object x) {
      final ReentrantLock lock = this.lock;
      lock.lock();
      try {
          // 查找对象在队列数组中的下标
          int i = indexOf(x);
          // 节点不存在，返回 false
          if (i < 0)
              return false;
  		// 修改元素的 heapIndex，-1 代表删除
          setIndex(queue[i], -1);
          // 尾索引是长度-1
          int s = --size;
          // 尾节点作为替代节点
          RunnableScheduledFuture<?> replacement = queue[s];
          queue[s] = null;
          // s == i 说明头节点就是尾节点，队列空了
          if (s != i) {
              // 向下调整
              siftDown(i, replacement);
              // 说明没发生调整
              if (queue[i] == replacement)
                  // 上移和下移不可能同时发生，替代节点大于子节点时下移，否则上移
                  siftUp(i, replacement);
          }
          return true;
      } finally {
          lock.unlock();
      }
  }
  ```

------

#### 成员方法

##### 提交任务

- schedule()：延迟执行方法，并指定执行的时间，默认是当前时间

  ```java
  public void execute(Runnable command) {
      // 以零延时任务的形式实现
      schedule(command, 0, NANOSECONDS);
  }
  ```

  ```java
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      // 判空
      if (command == null || unit == null) throw new NullPointerException();
      // 没有做任何操作，直接将 task 返回，该方法主要目的是用于子类扩展，并且【根据延迟时间设置任务触发的时间点】
      RunnableScheduledFuture<?> t = decorateTask(command, new ScheduledFutureTask<Void>(
          											command, null, triggerTime(delay, unit)));
      // 延迟执行
      delayedExecute(t);
      return t;
  }
  ```

  ```java
  // 返回【当前时间 + 延迟时间】，就是触发当前任务执行的时间
  private long triggerTime(long delay, TimeUnit unit) {
      // 设置触发的时间
      return triggerTime(unit.toNanos((delay < 0) ? 0 : delay));
  }
  long triggerTime(long delay) {
      // 如果 delay < Long.Max_VALUE/2，则下次执行时间为当前时间 +delay
      // 否则为了避免队列中出现由于溢出导致的排序紊乱,需要调用overflowFree来修正一下delay
      return now() + ((delay < (Long.MAX_VALUE >> 1)) ? delay : overflowFree(delay));
  }
  ```

  overflowFree 的原因：如果某个任务的 delay 为负数，说明当前可以执行（其实早该执行了）。阻塞队列中维护任务顺序是基于 compareTo 比较的，比较两个任务的顺序会用 time 相减。那么可能出现一个 delay 为正数减去另一个为负数的 delay，结果上溢为负数，则会导致 compareTo 产生错误的结果

  ```java
  private long overflowFree(long delay) {
      Delayed head = (Delayed) super.getQueue().peek();
      if (head != null) {
          long headDelay = head.getDelay(NANOSECONDS);
          // 判断一下队首的delay是不是负数，如果是正数就不用管，怎么减都不会溢出
          // 否则拿当前 delay 减去队首的 delay 来比较看，如果不出现上溢，排序不会乱
  		// 不然就把当前 delay 值给调整为 Long.MAX_VALUE + 队首 delay
          if (headDelay < 0 && (delay - headDelay < 0))
              delay = Long.MAX_VALUE + headDelay;
      }
      return delay;
  }
  ```

- scheduleAtFixedRate()：定时执行，一次任务的启动到下一次任务的启动的间隔

  ```java
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period,
                                                TimeUnit unit) {
      if (command == null || unit == null)
          throw new NullPointerException();
      if (period <= 0)
          throw new IllegalArgumentException();
      // 任务封装，【指定初始的延迟时间和周期时间】
      ScheduledFutureTask<Void> sft =new ScheduledFutureTask<Void>(command, null,
                                        triggerTime(initialDelay, unit), unit.toNanos(period));
      // 默认返回本身
      RunnableScheduledFuture<Void> t = decorateTask(command, sft);
      sft.outerTask = t;
      // 开始执行这个任务
      delayedExecute(t);
      return t;
  }
  ```

- scheduleWithFixedDelay()：定时执行，一次任务的结束到下一次任务的启动的间隔

  ```java
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
                                                   TimeUnit unit) {
      if (command == null || unit == null) 
          throw new NullPointerException();
      if (delay <= 0)
          throw new IllegalArgumentException();
      // 任务封装，【指定初始的延迟时间和周期时间】，周期时间为 - 表示是 fixed-delay 模式
      ScheduledFutureTask<Void> sft = new ScheduledFutureTask<Void>(command, null,
                                        triggerTime(initialDelay, unit), unit.toNanos(-delay));
      RunnableScheduledFuture<Void> t = decorateTask(command, sft);
      sft.outerTask = t;
      delayedExecute(t);
      return t;
  }
  ```

------

##### 运行任务

- delayedExecute()：**校验线程池状态**，延迟或周期性任务的主要执行方法

  ```java
  private void delayedExecute(RunnableScheduledFuture<?> task) {
      // 线程池是 SHUTDOWN 状态，需要执行拒绝策略
      if (isShutdown())
          reject(task);
      else {
          // 把当前任务放入阻塞队列，因为需要【获取执行时间最近的】，当前任务需要比较
          super.getQueue().add(task);
          // 线程池状态为 SHUTDOWN 并且不允许执行任务了，就从队列删除该任务，并设置任务的状态为取消状态
          if (isShutdown() && !canRunInCurrentRunState(task.isPeriodic()) && remove(task))
              task.cancel(false);
          else
              // 可以执行
              ensurePrestart();
      }
  }
  ```

- ensurePrestart()：**开启线程执行任务**

  ```java
  // ThreadPoolExecutor#ensurePrestart
  void ensurePrestart() {
      int wc = workerCountOf(ctl.get());
      // worker数目小于corePoolSize，则添加一个worker。
      if (wc < corePoolSize)
          // 第二个参数 true 表示采用核心线程数量限制，false 表示采用 maximumPoolSize
          addWorker(null, true);
      // corePoolSize = 0的情况，至少开启一个线程，【担保机制】
      else if (wc == 0)
          addWorker(null, false);
  }
  ```

- canRunInCurrentRunState()：任务运行时都会被调用以校验当前状态是否可以运行任务

  ```java
  boolean canRunInCurrentRunState(boolean periodic) {
      // 根据是否是周期任务判断，在线程池 shutdown 后是否继续执行该任务，默认非周期任务是继续执行的
      return isRunningOrShutdown(periodic ? continueExistingPeriodicTasksAfterShutdown :
                                 executeExistingDelayedTasksAfterShutdown);
  }
  ```

- onShutdown()：删除并取消工作队列中的不需要再执行的任务

  ```java
  void onShutdown() {
      BlockingQueue<Runnable> q = super.getQueue();
      // shutdown 后是否仍然执行延时任务
      boolean keepDelayed = getExecuteExistingDelayedTasksAfterShutdownPolicy();
      // shutdown 后是否仍然执行周期任务
      boolean keepPeriodic = getContinueExistingPeriodicTasksAfterShutdownPolicy();
      // 如果两者皆不可，则对队列中【所有任务】调用 cancel 取消并清空队列
      if (!keepDelayed && !keepPeriodic) {
          for (Object e : q.toArray())
              if (e instanceof RunnableScheduledFuture<?>)
                  ((RunnableScheduledFuture<?>) e).cancel(false);
          q.clear();
      }
      else {
          for (Object e : q.toArray()) {
              if (e instanceof RunnableScheduledFuture) {
                  RunnableScheduledFuture<?> t = (RunnableScheduledFuture<?>)e;
                  // 不需要执行的任务删除并取消，已经取消的任务也需要从队列中删除
                  if ((t.isPeriodic() ? !keepPeriodic : !keepDelayed) ||
                      t.isCancelled()) {
                      if (q.remove(t))
                          t.cancel(false);
                  }
              }
          }
      }
      // 因为任务被从队列中清理掉，所以需要调用 tryTerminate 尝试【改变线程池的状态】
      tryTerminate();
  }
  ```

------

### ForkJoin

Fork/Join：线程池的实现，体现是分治思想，适用于能够进行任务拆分的 CPU 密集型运算，用于**并行计算**

任务拆分：将一个大任务拆分为算法上相同的小任务，直至不能拆分可以直接求解。跟递归相关的一些计算，如归并排序、斐波那契数列都可以用分治思想进行求解

- Fork/Join 在**分治的基础上加入了多线程**，把每个任务的分解和合并交给不同的线程来完成，提升了运算效率
- ForkJoin 使用 ForkJoinPool 来启动，是一个特殊的线程池，默认会创建与 CPU 核心数大小相同的线程池
- 任务有返回值继承 RecursiveTask，没有返回值继承 RecursiveAction

```java
public static void main(String[] args) {
    ForkJoinPool pool = new ForkJoinPool(4);
    System.out.println(pool.invoke(new MyTask(5)));
    //拆分  5 + MyTask(4) --> 4 + MyTask(3) -->
}

// 1~ n 之间整数的和
class MyTask extends RecursiveTask<Integer> {
    private int n;

    public MyTask(int n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return "MyTask{" + "n=" + n + '}';
    }

    @Override
    protected Integer compute() {
        // 如果 n 已经为 1，可以求得结果了
        if (n == 1) {
            return n;
        }
        // 将任务进行拆分(fork)
        MyTask t1 = new MyTask(n - 1);
        t1.fork();
        // 合并(join)结果
        int result = n + t1.join();
        return result;
    }
}
```

继续拆分优化：

```java
class AddTask extends RecursiveTask<Integer> {
    int begin;
    int end;
    public AddTask(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }
    
    @Override
    public String toString() {
        return "{" + begin + "," + end + '}';
    }
    
    @Override
    protected Integer compute() {
        // 5, 5
        if (begin == end) {
            return begin;
        }
        // 4, 5  防止多余的拆分  提高效率
        if (end - begin == 1) {
            return end + begin;
        }
        // 1 5
        int mid = (end + begin) / 2; // 3
        AddTask t1 = new AddTask(begin, mid); // 1,3
        t1.fork();
        AddTask t2 = new AddTask(mid + 1, end); // 4,5
        t2.fork();
        int result = t1.join() + t2.join();
        return result;
    }
}
```

ForkJoinPool 实现了**工作窃取算法**来提高 CPU 的利用率：

- 每个线程都维护了一个**双端队列**，用来存储需要执行的任务
- 工作窃取算法允许空闲的线程从其它线程的双端队列中窃取一个任务来执行
- 窃取的必须是**最晚的任务**，避免和队列所属线程发生竞争，但是队列中只有一个任务时还是会发生竞争

------

### 享元模式

享元模式（Flyweight pattern）： 用于减少创建对象的数量，以减少内存占用和提高性能，这种类型的设计模式属于结构型模式，它提供了减少对象数量从而改善应用所需的对象结构的方式

异步模式：让有限的工作线程（Worker Thread）来轮流异步处理无限多的任务，也可将其归类为分工模式，典型实现就是线程池

工作机制：享元模式尝试重用现有的同类对象，如果未找到匹配的对象，则创建新对象

自定义连接池：

```java
public static void main(String[] args) {
    Pool pool = new Pool(2);
    for (int i = 0; i < 5; i++) {
        new Thread(() -> {
            Connection con = pool.borrow();
            try {
                Thread.sleep(new Random().nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pool.free(con);
        }).start();
    }
}
class Pool {
    //连接池的大小
    private final int poolSize;
    //连接对象的数组
    private Connection[] connections;
    //连接状态数组 0表示空闲  1表示繁忙
    private AtomicIntegerArray states;  //int[] -> AtomicIntegerArray

    //构造方法
    public Pool(int poolSize) {
        this.poolSize = poolSize;
        this.connections = new Connection[poolSize];
        this.states = new AtomicIntegerArray(new int[poolSize]);
        for (int i = 0; i < poolSize; i++) {
            connections[i] = new MockConnection("连接" + (i + 1));
        }
    }

    //使用连接
    public Connection borrow() {
        while (true) {
            for (int i = 0; i < poolSize; i++) {
                if (states.get(i) == 0) {
                    if (states.compareAndSet(i, 0, 1)) {
                        System.out.println(Thread.currentThread().getName() + " borrow " +  connections[i]);
                        return connections[i];
                    }
                }
            }
            //如果没有空闲连接，当前线程等待
            synchronized (this) {
                try {
                    System.out.println(Thread.currentThread().getName() + " wait...");
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //归还连接
    public void free(Connection con) {
        for (int i = 0; i < poolSize; i++) {
            if (connections[i] == con) {//判断是否是同一个对象
                states.set(i, 0);//不用cas的原因是只会有一个线程使用该连接
                synchronized (this) {
                    System.out.println(Thread.currentThread().getName() + " free " + con);
                    this.notifyAll();
                }
                break;
            }
        }
    }

}

class MockConnection implements Connection {
    private String name;
    //.....
}
```

------
