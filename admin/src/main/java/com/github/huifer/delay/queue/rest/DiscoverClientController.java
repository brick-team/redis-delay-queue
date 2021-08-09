package com.github.huifer.delay.queue.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.huifer.delay.queue.service.EurekaServerMetaDataService.DELAY_QUEUE_TASK_KEY;

@RestController
@RequestMapping("/discovery")
public class DiscoverClientController {
	@Autowired
	private DiscoveryClient discoveryClient;


	@GetMapping("/service_info")
	public Object hhh() {
		Map<String, Object> ins = new HashMap<>();


		List<String> services = discoveryClient.getServices();
		for (String service : services) {
			List<ServiceInstance> instances = discoveryClient.getInstances(service);

			List<Map<String, Object>> maps = new ArrayList<>();
			for (ServiceInstance instance : instances) {
				Map<String, String> metadata = instance.getMetadata();
				String taskType = metadata.get(DELAY_QUEUE_TASK_KEY);
				Map<String, Object> data = new HashMap<>();
				data.put("ServiceInstance", instance);
				data.put("TaskType", taskType);
				if (StringUtils.hasLength(taskType)) {
					maps.add(data);
				}
			}
			ins.put(service, maps);
		}
		return ins;
	}

}
