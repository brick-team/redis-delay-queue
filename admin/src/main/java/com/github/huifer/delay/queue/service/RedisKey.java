package com.github.huifer.delay.queue.service;

public interface RedisKey {
	String LOCK_KEY = "delay_queue:lock:";
	String BUCKET_REDIS_KEY = "delay_queue:bucket:";
	String REDIS_DELAY_QUEUE_POOL_KEY = "delay_queue:delayQueuePool";
	String DELAY_QUEUE_TYPE_KEY = "delay_queue:taskType";
	String SERVER_HISTORY_KEY = "delay_queue:server:history:";

}
