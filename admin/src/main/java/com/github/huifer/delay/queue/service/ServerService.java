package com.github.huifer.delay.queue.service;

import com.github.huifer.delay.queue.domain.ServerInfo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ServerService {
	public static final String SERVER_KEY = RedisKey.SERVER_HISTORY_KEY;
	private final StringRedisTemplate stringRedisTemplate;
	private final Gson gson;
	private final DelayQueueTypeService delayQueueTypeService;

	public ServerService(StringRedisTemplate stringRedisTemplate, Gson gson, DelayQueueTypeService delayQueueTypeService) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.gson = gson;
		this.delayQueueTypeService = delayQueueTypeService;
	}

	/**
	 * 注册服务
	 */
	public void register(String taskType, String url) {
		ServerInfo serverInfo = new ServerInfo();
		serverInfo.setTimestamp(System.currentTimeMillis());
		serverInfo.setUrl(url);
		this.stringRedisTemplate.opsForList().rightPush(SERVER_KEY + taskType, gson.toJson(serverInfo));
	}

	public void remove(String taskType, ServerInfo serverInfo) {
		this.stringRedisTemplate.opsForList().remove(SERVER_KEY + taskType, 0, gson.toJson(serverInfo));
	}

	/**
	 * 服务列表
	 */
	public List<ServerInfo> list(String taskType) {
		List<String> range = this.stringRedisTemplate.opsForList().range(SERVER_KEY + taskType, 0, -1);
		List<ServerInfo> serverInfos = new ArrayList<>();
		for (String s : range) {
			serverInfos.add(gson.fromJson(s, ServerInfo.class));
		}
		return serverInfos;
	}

	/**
	 * 健康检查
	 */
	public void beat(String taskType, String url) {
		this.register(taskType, url);
	}

	@Scheduled(cron = "0/5 * * * * ?")
	public void handlerHeartbeat() {
		log.info("开始处理健康检查相关问题");
		Set<String> types = delayQueueTypeService.list();
		for (String type : types) {
			List<ServerInfo> serverInfos = this.list(type);
			for (ServerInfo serverInfo : serverInfos) {
				long sub = serverInfo.getTimestamp() - System.currentTimeMillis();
				// 时间差大于10秒移除
				if (sub >= 10000) {
					this.remove(type, serverInfo);
				}
			}
		}
	}


}
