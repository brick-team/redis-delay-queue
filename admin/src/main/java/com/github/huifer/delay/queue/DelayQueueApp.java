package com.github.huifer.delay.queue;

import com.github.huifer.delay.queue.config.DelayQueueConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		DelayQueueConfig.class
})
public class DelayQueueApp {
	public static void main(String[] args) {
		SpringApplication.run(DelayQueueApp.class, args);
	}
}
