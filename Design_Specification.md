# Redis延迟队列设计说明
## 延迟队列核心设计
本项目使用redis作为延迟队列的核心中间件，主要使用到的数据结构有zset、list和hash。首先需要确认延迟队列的一些关键对象，第一个是任务实体对象（DelayQueueJob）该对象用于提交任务，基础定义如下。

```java
public class DelayQueueJob {
   /**
    * 任务id
    */
   private String taskId;
   /**
    * 任务类型
    */
   private String taskType;
   /**
    * 执行时间，16位时间戳
    */
   private long delayTime;
   /**
    * 参数,json表示
    */
   private String params;
}
```

第二个对象是任务描述，主要用于简化存储，其数据来源是DelayQueueJob，基础定义代码如下。

```java
public class TaskDetail {

   /**
    * 任务id
    */
   private String taskId;
   /**
    * 执行时间，16位时间戳
    */
   private long delayTime;
}
```

基础对象确认完成后下面需要进行redis相关使用，首先需要记录所有的任务信息（DelayQueueJob），这里选择使用hash数据结构进行存储,其中key是任务ID（taskId)，value是DelayQueueJob对象的json表示。其次需要记录任务执行时间，记录该数据需要使用TaskDetail对象和zset数据结构，使用的目标是应为可以直接排序获取最小值，但是在restTemplate这个对象中并未提供这个获取方式所以在代码实现上是手动排序。最后还需要存储所有的任务类型，存储结构是list。

核心处理流程如下

1. 在zset中找到最小的数据对象，这个最小是指时间戳最小。在最小对象寻找时会有如下几个情况
   1. 最小对象为空，空转1秒进行下一次轮询。
   2. 最小对象的任务开始时间大于当前时间，空转1秒进行下一次轮询。
   3. 最小对象存在并且可以通过任务id找到对应的DelayQueueJob则触发处理。在处理后删除zset中的数据。



```java
@Override
public void run() {
   while (true) {
      // 取最小对象
      TaskDetail taskDetail = bucketService.getMin(taskType);
      // 最小对象为空不处理
      if (taskDetail == null) {
         sleep();
         continue;
      }
      // 最小对象的任务开始时间大于当前时间不处理
      if (taskDetail.getDelayTime() > System.currentTimeMillis()) {
         sleep();
         continue;
      }

      DelayQueueJob delayQueue = poolService.getDelayQueue(taskDetail.getTaskId());
      // 延迟任务信息不存在删除最小对象
      if (delayQueue == null) {
         bucketService.del(taskType, taskDetail);
         continue;
      }

      if (delayQueue.getDelayTime() <= System.currentTimeMillis()) {
         log.info("执行任务, dt = [{}]", delayQueue);
         boolean lock = redisLockHelper.get(PK + delayQueue.getTaskId(), delayQueue.getTaskId(), 10);
         if (lock) {
            // 执行任务
            
            bucketService.del(taskType, taskDetail);
            redisLockHelper.release(PK + delayQueue.getTaskId(), delayQueue.getTaskId());
         }

      }
   }
}
```

## 插件模式

插件模式的实现是将核心处理逻辑下沉到各个使用插件的应用中，核心处理是需要创建多个线程根据任务类型处理独自的任务，核心代码如下

```java
public void register(String taskType, int sec) {
    log.info("注册任务类型=[{}],间隔扫描时间=[{}]", taskType, sec);
    executorService.execute(new DelayBucketHandler(
        taskType,
        bucketService,
        poolService,
        sec,
        findTaskWorkers(taskType),
        redisLockHelper, logService));
}
```

在这段代码中需要设置任务类型和间隔时间，其中间隔时间建议是1秒可以相对准确的定位时间，时间准确度要求高的情况下可以修改底层逻辑改为毫秒级别。在这段代码中会提交一个线程，因此还需要关注一个可以设置线程数量的方法，具体代码如下

```java
protected void setThreads(int size) {
   executorService = Executors.newFixedThreadPool(size);
}
```

上述这两个方法都需要在插件模式中进行实现或者调用。这些都属于准备工作层面的内容，实际开发需要实现任务处理接口以及调用任务提交接口，先说处理任务接口，对象是TaskWorker详细定义如下。

```java
public interface TaskWorker {
   /**
    * 任务处理
    */
   void invoke(String message);

   /**
    * 定时任务类型
    */
   String type();


   /**
    * 填写当前类
    */
   Class<?> clazz();

}
```

在所接入项目中需要实现TaskWorker，本项目是基于Spring框架进行开发因此需要在实现TaskWorker时需要交给Spring管理该对象，通过交给Spring管理后在注册时才可以通过任务类型在Spring容器中找到所有的TaskWorker达到自动注入。 

**劣势：需要占用额外的线程**

## 服务模式

下面将介绍服务模式相关设计，在插件模式中已经简单完成了基础的处理操作，也总结了插件模式的劣势，为了解决这个劣势引出了服务模式，服务模式需要启动一个独立引用来作为调度中心。调度中心的核心还是redis的使用，将下沉到应用的代码放在了中央进行处理，由接入服务模式的应用向延迟队列服务提交任务。目前接入的是eureka，在服务模式下客户端需要将一些基础信息进行配置.**注意：目前采用的是自动注入的方式**。在客户端中需要使用到eureka中的元数据，这里需要使用到ApplicationInfoManager来进行元数据注册，这里所需要使用的元数据是TaskWorker接口的任务类型，在客户端层面还会默认注入一个Controlelr对象，该对象用于接收服务端发送请求来唤醒应用本身的处理。**注意：服务模式下回产生服务通讯，通讯可靠性无法保证**。

客户端启动流程。

1. 提取应用名称。
2. 提取项目中交给Spring管理的TaskWorker实例
3. 将第二步中得到的所有TaskWorker提取任务类型
4. 通过eureka的元数据保存任务类型

客户端使用流程。

1. 注入TaskSubmitService调用send方法进行延迟队列数据提交。

服务端接收处理流程。

1. 接收客户端发送的数据，将应用名称和任务类型作为键进行相关数据存储。
2. 判断服务端是否已经存在键（应用名称和任务类型）的处理线程，如果不存在会进行创建。

服务端时间到达后的处理流程。

1. 从eureka的服务表中找到对应的应用对象获取接口地址，发送指定接口的相关数据。





## 重启处理

在本项目设计中对项目的重启做出了额外的处理，当项目重启后会在redis中找到历史的任务类型（taskType）集合，并将其创建相关的任务处理线程（DelayBucketHandler）从而来完成自动数据补发的操作。