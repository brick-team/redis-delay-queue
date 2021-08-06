package com.github.huifer.delay.queue.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dc")
public class DiscoverClientController {
	@Autowired
	private DiscoveryClient discoveryClient;
	@GetMapping("/ddd")
	public void hhh(){
		List<ServiceInstance> sev = this.discoveryClient.getInstances("sev");
		System.out.println();
	}
}
