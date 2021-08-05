package com.github.huifer.delay.queue.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class DelayQueueTypeService {
	public static final String DELAY_QUEUE_TYPE_KEY = "delay_queue:taskType";
	private final StringRedisTemplate stringRedisTemplate;

	public DelayQueueTypeService(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	public void add(String taskType) {
		this.stringRedisTemplate.opsForSet().add(DELAY_QUEUE_TYPE_KEY, taskType);
	}

	public Set<String> list() {
		return this.stringRedisTemplate.opsForSet().members(DELAY_QUEUE_TYPE_KEY);

	}

	public void del(String taskType) {
		this.stringRedisTemplate.opsForSet().remove(DELAY_QUEUE_TYPE_KEY, taskType);
	}
}
