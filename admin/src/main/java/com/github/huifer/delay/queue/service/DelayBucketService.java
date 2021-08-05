package com.github.huifer.delay.queue.service;

import com.github.huifer.delay.queue.domain.TaskDetail;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class DelayBucketService {
	public static final String BUCKET_REDIS_KEY = "delay_queue:bucket:";
	private final StringRedisTemplate stringRedisTemplate;
	private final Gson gson;

	public DelayBucketService(StringRedisTemplate stringRedisTemplate, Gson gson) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.gson = gson;
	}

	/**
	 * @param bucketKey
	 * @param taskDetail
	 */
	public void add(String bucketKey, TaskDetail taskDetail) {
		stringRedisTemplate.opsForZSet().add(BUCKET_REDIS_KEY + bucketKey, gson.toJson(taskDetail), taskDetail.getDelayTime());
	}

	/**
	 * @param bucketKey
	 * @param taskDetail
	 */
	public void del(String bucketKey, TaskDetail taskDetail) {
		stringRedisTemplate.opsForZSet().remove(BUCKET_REDIS_KEY + bucketKey, gson.toJson(taskDetail));
	}

	/**
	 * @param bucketKey
	 * @return
	 */
	public TaskDetail getMin(String bucketKey) {
		Set<String> range = this.stringRedisTemplate.opsForZSet().range(BUCKET_REDIS_KEY + bucketKey, 0, -1);
		if (CollectionUtils.isEmpty(range)) {
			return null;
		}
		Set<TaskDetail> details = new HashSet<>(range.size());
		for (String s : range) {
			details.add(gson.fromJson(s, TaskDetail.class));
		}
		Optional<TaskDetail> min = details.stream().min(Comparator.comparingLong(TaskDetail::getDelayTime));
		if (min.isPresent()) {
			return min.get();
		}
		return null;
	}

}
