package com.github.huifer.delay.queue.rest;

import com.github.huifer.delay.queue.domain.DelayQueueJob;
import com.github.huifer.delay.queue.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
@RestController
@RequestMapping("/job")
public class JobController {
	private final TaskSubmitService taskSubmitService;

	private final DelayQueueTypeService delayQueueTypeService;

	private final ExecutorService taskExecutorService;

	private final DelayBucketService bucketService;

	private final DelayQueuePoolService poolService;

	private final RedisLockServiceImpl redisLockHelper;
	@Autowired
	private ApplicationContext context;

	public JobController(
			TaskSubmitService taskSubmitService,
			@Qualifier("taskExecutorService") ExecutorService taskExecutorService,
			RedisLockServiceImpl redisLockHelper,
			DelayBucketService bucketService,
			DelayQueuePoolService poolService,
			DelayQueueTypeService delayQueueTypeService
	) {
		this.taskSubmitService = taskSubmitService;
		this.taskExecutorService = taskExecutorService;
		this.redisLockHelper = redisLockHelper;
		this.bucketService = bucketService;
		this.poolService = poolService;
		this.delayQueueTypeService = delayQueueTypeService;
	}

	@PostMapping("/add")
	public void add(
			@Validated @RequestBody DelayQueueJob delayQueueJobEntity
	) {
		log.info("接受数据=[{}]", delayQueueJobEntity);
		if (!delayQueueTypeService.list().contains(generatorTypeName(delayQueueJobEntity))) {
			delayQueueTypeService.add(generatorTypeName(delayQueueJobEntity));
			createHandler(delayQueueJobEntity);
		}
		taskSubmitService.submitTask(delayQueueJobEntity);

	}

	private String generatorTypeName(@RequestBody @Validated DelayQueueJob delayQueueJobEntity) {
		return delayQueueJobEntity.getAppName() + "-" + delayQueueJobEntity.getTaskType();
	}

	private void createHandler(@RequestBody @Validated DelayQueueJob delayQueueJobEntity) {
		taskExecutorService.execute(new DelayBucketHandler(
				generatorTypeName(delayQueueJobEntity),
				bucketService,
				poolService,
				1,
				redisLockHelper, findDelayQueueJobHandler()));
	}

	private Collection<DelayQueueJobHandler> findDelayQueueJobHandler() {
		Map<String, DelayQueueJobHandler> beansOfType = context.getBeansOfType(DelayQueueJobHandler.class);
		Collection<DelayQueueJobHandler> values = beansOfType.values();
		return values;
	}
}
