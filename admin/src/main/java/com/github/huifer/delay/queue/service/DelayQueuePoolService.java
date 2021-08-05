package com.github.huifer.delay.queue.service;

import com.github.huifer.delay.queue.domain.DelayQueueJob;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DelayQueuePoolService {
	public static final String REDIS_DELAY_QUEUE_POOL_KEY = RedisKey.REDIS_DELAY_QUEUE_POOL_KEY;
	private final StringRedisTemplate stringRedisTemplate;
	private final Gson gson;

	public DelayQueuePoolService(StringRedisTemplate stringRedisTemplate, Gson gson) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.gson = gson;
	}

	/**
	 * 添加延迟队列任务
	 */
	public void addDelayQueue(DelayQueueJob delayQueueJobEntity) {
		this.stringRedisTemplate.opsForHash().put(
				REDIS_DELAY_QUEUE_POOL_KEY,
				delayQueueJobEntity.getTaskId(),
				gson.toJson(delayQueueJobEntity));
	}

	/**
	 * 根据任务id获取任务信息
	 */
	public DelayQueueJob getDelayQueue(String taskId) {
		Map<Object, Object> entries = this.stringRedisTemplate.opsForHash().entries(REDIS_DELAY_QUEUE_POOL_KEY);
		Object o = entries.get(taskId);
		return gson.fromJson((String) o, DelayQueueJob.class);
	}

	/**
	 * 根据任务id删除任务
	 */
	public void deleteDelayQueue(String taskId) {
		this.stringRedisTemplate.opsForHash().delete(REDIS_DELAY_QUEUE_POOL_KEY, taskId);
	}
}
