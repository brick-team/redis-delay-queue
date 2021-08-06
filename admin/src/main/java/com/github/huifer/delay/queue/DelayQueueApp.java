package com.github.huifer.delay.queue;

import com.github.huifer.delay.queue.config.DelayQueueConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({
		DelayQueueConfig.class
})
@EnableScheduling
@EnableDiscoveryClient
public class DelayQueueApp {
	public static void main(String[] args) {
		SpringApplication.run(DelayQueueApp.class, args);
	}
}
