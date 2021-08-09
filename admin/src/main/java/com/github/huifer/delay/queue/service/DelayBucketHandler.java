package com.github.huifer.delay.queue.service;

import com.github.huifer.delay.queue.domain.DelayQueueJob;
import com.github.huifer.delay.queue.domain.TaskDetail;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static com.github.huifer.delay.queue.service.RedisKey.LOCK_KEY;

@Slf4j
public class DelayBucketHandler implements Runnable {
	public static final String PK = LOCK_KEY;
	private final String taskType;
	private final DelayBucketService bucketService;
	private final DelayQueuePoolService poolService;
	private final long timeSleep;
	private final RedisLockServiceImpl redisLockHelper;
	private final Collection<DelayQueueJobHandler> delayQueueJobHandlers;
	Gson gson = new Gson();

	public DelayBucketHandler(String delayBucketKey, DelayBucketService bucketService,
							  DelayQueuePoolService poolService, long timeSleep,
							  RedisLockServiceImpl redisLockHelper, Collection<DelayQueueJobHandler> delayQueueJobHandlers) {
		this.taskType = delayBucketKey;
		this.bucketService = bucketService;
		this.poolService = poolService;
		this.timeSleep = timeSleep;
		this.redisLockHelper = redisLockHelper;
		this.delayQueueJobHandlers = delayQueueJobHandlers;
	}

	@Override
	public void run() {
		while (true) {
			// 取最小对象
			TaskDetail taskDetail = bucketService.getMin(taskType);
			log.info("处理任务类型 = [{}], taskDetail= [{}]", taskType, taskDetail);
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
					for (DelayQueueJobHandler delayQueueJobHandler : delayQueueJobHandlers) {
						delayQueueJobHandler.work(delayQueue);
					}
					bucketService.del(taskType, taskDetail);
					redisLockHelper.release(PK + delayQueue.getTaskId(), delayQueue.getTaskId());
				}

			}
		}
	}

	private void sleep() {
		try {
			TimeUnit.SECONDS.sleep(timeSleep);
		} catch (InterruptedException e) {
			log.error("", e);
		}
	}
}
