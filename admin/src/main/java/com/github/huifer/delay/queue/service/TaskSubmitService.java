package com.github.huifer.delay.queue.service;

import com.github.huifer.delay.queue.domain.DelayQueueJob;
import com.github.huifer.delay.queue.domain.TaskDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskSubmitService {
	private final DelayBucketService bucketService;
	private final DelayQueuePoolService delayQueuePoolService;

	public TaskSubmitService(DelayBucketService bucketService, DelayQueuePoolService delayQueuePoolService) {
		this.bucketService = bucketService;
		this.delayQueuePoolService = delayQueuePoolService;
	}

	public void submitTask(DelayQueueJob delayQueueJobEntity) {
		delayQueuePoolService.addDelayQueue(delayQueueJobEntity);
		TaskDetail taskDetail = new TaskDetail();
		taskDetail.setTaskId(delayQueueJobEntity.getTaskId());
		taskDetail.setDelayTime(delayQueueJobEntity.getDelayTime());
		bucketService.add(delayQueueJobEntity.getTaskType(), taskDetail);
	}
}
