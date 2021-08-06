package com.github.huifer.delay.queue.example.sb.runner;

import com.github.huifer.delay.queue.plugin.register.AbstractTaskRegister;
import com.github.huifer.delay.queue.plugin.service.DelayBucketService;
import com.github.huifer.delay.queue.plugin.service.DelayQueuePoolService;
import com.github.huifer.delay.queue.plugin.service.LogService;
import com.github.huifer.delay.queue.plugin.service.RedisLockServiceImpl;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

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
