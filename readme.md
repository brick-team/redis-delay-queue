# Redis Delay Queue

基于redis的延迟队列，技术参考[有赞延迟队列](https://tech.youzan.com/queuing_delay/)

## 插件版使用说明

引入`redis-delay-queue-plugin`依赖，具体依赖如下

```
<dependency>
   <groupId>com.github.huifer</groupId>
   <artifactId>embedded</artifactId>
   <version>1.0-SNAPSHOT</version>
</dependency>
```

实现`com.github.huifer.delay.queue.plugin.service.TaskWorker`接口，在TaskWorker中有三个方法

1. 方法invoke用于编写具体的执行逻辑，其中方法参数来源是`com.github.huifer.delay.queue.plugin.domain.DelayQueueJob#params`
2. 方法type用于表示任务类型，该数据需要和`com.github.huifer.delay.queue.plugin.domain.DelayQueueJob#taskType`对应
3. 方法clazz用于表示处理类的类。

测试用代码如下

```
@Service
@Slf4j
public class Task1 implements TaskWorker {
   @Override
   public void invoke(String message) {
      log.info("message = [{}]", message);
   }

   @Override
   public String type() {
      return "task1";
   }

   @Override
   public Class<?> clazz() {
      return Task2.class;
   }
}

@Slf4j
@Service
public class Task2 implements TaskWorker {
	@Override
	public void invoke(String message) {
		log.info("message = [{}]", message);
	}

	@Override
	public String type() {
		return "task2";
	}

	@Override
	public Class<?> clazz() {
		return Task2.class;
	}
}

@Slf4j
@Service
public class Task2Copy implements TaskWorker {
	@Override
	public void invoke(String message) {
		log.info("message=[{}]", message);
	}

	@Override
	public String type() {
		return "task2";
	}

	@Override
	public Class<?> clazz() {
		return Task2Copy.class;
	}
}
```

实现AbstractTaskRegister接口具体实现的是run方法，在run方法中需要设置线程数量以及注册任务处理器，演示代码如下

```java
@Component
public class TaskRunner extends AbstractTaskRegister {
   public TaskRunner(DelayBucketService bucketService, DelayQueuePoolService poolService, RedisLockServiceImpl redisLockHelper, LogService logService) {
      super(bucketService, poolService, redisLockHelper, logService);
   }

   @Override
   public void run(ApplicationArguments args) throws Exception {
      this.setThreads(10);
      this.register("task1", 1);
      this.register("task2", 1);
   }
}
```

提交延迟队列任务，具体需要使用`com.github.huifer.delay.queue.plugin.service.TaskSubmitService`，演示代码如下

```java
@RestController
@RequestMapping("/job")
public class JobCtr {
   final TaskSubmitService taskSubmitService;

   public JobCtr(TaskSubmitService taskSubmitService) {
      this.taskSubmitService = taskSubmitService;
   }

   @GetMapping("/add")
   public void addJob(@RequestBody DelayQueueJob delayQueueJobEntity) {
      this.taskSubmitService.submitTask(delayQueueJobEntity);
   }
}
```

在上述测试用例中定义了两个任务类型:`task1`和`task2`，通过`/job/add`接口提交一个延迟任务，具体提交测试请求用例如下

```http
GET http://localhost:9024/job/add
Content-Type: application/json

{
  "taskId": "123",
  "taskType": "task2",
  "delayTime": "1628146026000",
  "params": "啊啊啊啊啊啊啊啊啊"
}
```

发送后由于`delayTime`的时间小于当前时间会直接执行，执行效果如下

```
2021-08-06 13:41:56.626  INFO 17852 --- [pool-1-thread-2] c.g.h.d.q.p.service.DelayBucketHandler   : 执行任务, dt = [DelayQueueJob{taskId='123', taskType='task2', delayTime=1628146026000, params='啊啊啊啊啊啊啊啊啊'}]
2021-08-06 13:41:56.668  INFO 17852 --- [pool-1-thread-2] c.g.h.d.queue.example.sb.service.Task2   : message = [啊啊啊啊啊啊啊啊啊]
2021-08-06 13:41:56.668  INFO 17852 --- [pool-1-thread-2] c.g.h.d.q.p.service.DelayBucketHandler   : 任务执行成功,任务类型=[task2],任务id=[task2],执行类是=[class com.github.huifer.delay.queue.example.sb.service.Task2],执行参数是=[啊啊啊啊啊啊啊啊啊]
2021-08-06 13:41:56.679  INFO 17852 --- [pool-1-thread-2] c.g.h.d.q.example.sb.service.Task2Copy   : message=[啊啊啊啊啊啊啊啊啊]
2021-08-06 13:41:56.680  INFO 17852 --- [pool-1-thread-2] c.g.h.d.q.p.service.DelayBucketHandler   : 任务执行成功,任务类型=[task2],任务id=[task2],执行类是=[class com.github.huifer.delay.queue.example.sb.service.Task2Copy],执行参数是=[啊啊啊啊啊啊啊啊啊]

```

**注意：插件版本的底层基于线程死循环，允许配置间隔时间建议间隔时间设置1秒**





## Spring Cloud Eureka 版本接入

添加pom依赖

```xml
<dependency>
   <groupId>com.github.huifer</groupId>
   <artifactId>embedded-web</artifactId>
   <version>1.0-SNAPSHOT</version>
</dependency>
```

修改配置文件，总共注意两项：

1. 添加**`spring.application.name`**。
2. 添加**`delay-queue.center-url`**，该数据为admin项目的url地址。

实现`com.github.huifer.delay.queue.plugin.web.TaskWorker`接口，在TaskWorker中有三个方法

1. 方法invoke用于编写具体的执行逻辑。
2. 方法type用于表示任务类型。
3. 方法clazz用于表示处理类的类。

任务提交通过`com.github.huifer.delay.queue.plugin.web.TaskSubmitService`进行提交

**注意：使用eureka版本需要首先启动admin工程**