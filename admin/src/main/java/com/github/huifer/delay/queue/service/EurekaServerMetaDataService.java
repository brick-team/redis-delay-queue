package com.github.huifer.delay.queue.service;

import com.github.huifer.delay.queue.domain.DelayQueueJob;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EurekaServerMetaDataService implements DelayQueueJobHandler {
	public static final String DELAY_QUEUE_TASK_KEY = "delay-queue:task";
	public static final String SEND_URL = "/hf/delay_queue/work";
	private final DiscoveryClient discoveryClient;
	private final RestTemplate restTemplate;
	private final Gson gson;

	public EurekaServerMetaDataService(DiscoveryClient discoveryClient, RestTemplate restTemplate, Gson gson) {
		this.discoveryClient = discoveryClient;
		this.restTemplate = restTemplate;
		this.gson = gson;
	}

	public void work(DelayQueueJob delayQueueJob) {
		// 获取应用名称
		String appName = delayQueueJob.getAppName();
		// 获取任务类型
		String taskType = delayQueueJob.getTaskType();

		// 获取应用名称对应的实例
		List<ServiceInstance> instances = discoveryClient.getInstances(appName);
		for (ServiceInstance instance : instances) {
			Map<String, String> metadata = instance.getMetadata();
			String taskTypes = metadata.get(DELAY_QUEUE_TASK_KEY);
			if (StringUtils.hasLength(taskTypes)) {
				if (taskTypes.contains(taskType)) {
					URI uri = instance.getUri();
					String url = uri.toString();
					if (send(delayQueueJob, url)) {
						continue;
					}
				}
			}
		}
	}

	public boolean send(DelayQueueJob delayQueueJob, String uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> request = new HttpEntity<>(gson.toJson(delayQueueJob), headers);
		ResponseEntity<String> postForEntity = restTemplate.postForEntity(
				uri + SEND_URL,
				request,
				String.class
		);
		String body = postForEntity.getBody();
		log.info("发生请求,地址=[{}],参数=[{}],返回值=[{}]", uri + SEND_URL, delayQueueJob, body);
		int statusCodeValue = postForEntity.getStatusCodeValue();
		return statusCodeValue == 200;
	}
}
