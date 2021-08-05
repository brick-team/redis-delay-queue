package com.github.huifer.delay.queue.rest;

import com.github.huifer.delay.queue.domain.DelayQueueJob;
import com.github.huifer.delay.queue.service.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/job")
public class JobController {
	private final TaskSubmitService taskSubmitService;

	private final DelayQueueTypeService delayQueueTypeService;

	private final ExecutorService taskExecutorService;

	private final DelayBucketService bucketService;

	private final DelayQueuePoolService poolService;

	private final RedisLockServiceImpl redisLockHelper;


	public JobController(TaskSubmitService taskSubmitService,
						 @Qualifier("taskExecutorService") ExecutorService taskExecutorService, RedisLockServiceImpl redisLockHelper, DelayBucketService bucketService, DelayQueuePoolService poolService, DelayQueueTypeService delayQueueTypeService) {
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

		if (delayQueueTypeService.list().contains(delayQueueJobEntity.getTaskType())) {

			createHandler(delayQueueJobEntity);
		} else {
			delayQueueTypeService.add(delayQueueJobEntity.getTaskType());
			createHandler(delayQueueJobEntity);
		}
		taskSubmitService.submitTask(delayQueueJobEntity);

	}

	private void createHandler(@RequestBody @Validated DelayQueueJob delayQueueJobEntity) {
		taskExecutorService.execute(new DelayBucketHandler(
				delayQueueJobEntity.getTaskType(),
				bucketService,
				poolService,
				1,
				redisLockHelper));
	}
}
