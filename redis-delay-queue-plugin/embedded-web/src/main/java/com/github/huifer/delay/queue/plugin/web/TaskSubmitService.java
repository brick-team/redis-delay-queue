package com.github.huifer.delay.queue.plugin.web;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TaskSubmitService {

	Gson gson = new Gson();
	@Autowired
	@Qualifier("delayQueueTemplate")
	private RestTemplate restTemplate;
	@Autowired
	private CenterConfig centerConfig;

	public void send(DelayQueueJob delayQueueJob) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> request = new HttpEntity<>(gson.toJson(delayQueueJob), headers);
		ResponseEntity<String> postForEntity = restTemplate.postForEntity(
				centerConfig.getCenterUrl() + "/job/add",
				request,
				String.class
		);
		String body = postForEntity.getBody();
	}
}
