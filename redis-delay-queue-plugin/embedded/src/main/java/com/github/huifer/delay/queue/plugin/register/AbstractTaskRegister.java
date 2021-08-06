package com.github.huifer.delay.queue.plugin.register;

import com.github.huifer.delay.queue.plugin.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public abstract class AbstractTaskRegister implements ApplicationRunner, ApplicationContextAware {
	private final DelayBucketService bucketService;
	private final DelayQueuePoolService poolService;
	private final RedisLockServiceImpl redisLockHelper;
	private final LogService logService;
	ExecutorService executorService;
	ApplicationContext applicationContext;

	public AbstractTaskRegister(DelayBucketService bucketService, DelayQueuePoolService poolService,
								RedisLockServiceImpl redisLockHelper, LogService logService) {
		this.bucketService = bucketService;
		this.poolService = poolService;
		this.redisLockHelper = redisLockHelper;
		this.logService = logService;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	protected void setThreads(int size) {
		executorService = Executors.newFixedThreadPool(size);
	}

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

	private List<TaskWorker> findTaskWorkers(String taskType) {
		Map<String, TaskWorker> beansOfType = applicationContext.getBeansOfType(TaskWorker.class);
		List<TaskWorker> taskWorkers = new ArrayList<>();

		beansOfType.forEach((k, v) -> {
			boolean equals = v.type().equals(taskType);
			if (equals) {
				taskWorkers.add(v);
			}
		});
		return taskWorkers;
	}

}
