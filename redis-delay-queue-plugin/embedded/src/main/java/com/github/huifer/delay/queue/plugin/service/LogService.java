package com.github.huifer.delay.queue.plugin.service;

import com.google.gson.Gson;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogService {
	public static final String JOB_LOG_OK = RedisKey.JOB_LOG_OK;
	public static final String JOB_LOG_ERROR = RedisKey.JOB_LOG_ERROR;

	private final StringRedisTemplate stringRedisTemplate;

	private final Gson gson;

	public LogService(StringRedisTemplate stringRedisTemplate, Gson gson) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.gson = gson;
	}

	public void logOk(String taskType, String taskId, String className, String param) {
//        Map<String, String> data = new HashMap<>(2);
//        data.put("taskId", taskId);
//        data.put("className", className);
//        data.put("param", param);
//        stringRedisTemplate.opsForList()
//                .leftPush(JOB_LOG_OK + taskType, gson.toJson(data));
	}

	public void logError(String taskType, String taskId, String className, String param) {
//        Map<String, String> data = new HashMap<>(2);
//        data.put("taskId", taskId);
//        data.put("className", className);
//        data.put("param", param);
//        stringRedisTemplate.opsForList()
//                .leftPush(JOB_LOG_ERROR + taskType, gson.toJson(data));
	}


}
