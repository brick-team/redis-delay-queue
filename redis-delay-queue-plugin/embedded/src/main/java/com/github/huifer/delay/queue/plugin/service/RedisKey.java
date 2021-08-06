package com.github.huifer.delay.queue.plugin.service;

public interface RedisKey {
	String LOCK_KEY = "delay_queue:lock:";
	String BUCKET_REDIS_KEY = "delay_queue:bucket:";
	String REDIS_DELAY_QUEUE_POOL_KEY = "delay_queue:delayQueuePool";
	String DELAY_QUEUE_TYPE_KEY = "delay_queue:taskType";
	String SERVER_HISTORY_KEY = "delay_queue:server:history:";
	String JOB_LOG_OK = "delay_queue:job:ok";
	String JOB_LOG_ERROR = "delay_queue:job:error";

}
